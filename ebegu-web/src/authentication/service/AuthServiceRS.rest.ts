/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import TSUser from '../../models/TSUser';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import HttpBuffer from './HttpBuffer';
import ICookiesService = angular.cookies.ICookiesService;
import IRootScopeService = angular.IRootScopeService;

export default class AuthServiceRS {

    private principal: TSUser;

    static $inject = ['$http', 'CONSTANTS', '$q', '$timeout', '$cookies', 'base64', 'EbeguRestUtil', 'httpBuffer', '$rootScope'];

    /* @ngInject */
    constructor(private $http: IHttpService, private CONSTANTS: any, private $q: IQService,
                private $timeout: ITimeoutService,
                private $cookies: ICookiesService, private base64: any, private ebeguRestUtil: EbeguRestUtil,
                private httpBuffer: HttpBuffer,
                private $rootScope: IRootScopeService) {
    }

    public getPrincipal(): TSUser {
        return this.principal;
    }

    public getPrincipalRole(): TSRole {
        if (this.principal) {
            return this.principal.role;
        }
        return undefined;
    }

    public loginRequest(userCredentials: TSUser): IPromise<TSUser> {
        if (userCredentials) {
            return this.$http.post(this.CONSTANTS.REST_API + 'auth/login', this.ebeguRestUtil.userToRestObject({}, userCredentials))
                .then((response: any) => {

                    // try to reload buffered requests
                    this.httpBuffer.retryAll((config: IRequestConfig) => {
                        return config;
                    });
                    //ensure that there is ALWAYS a logout-event before the login-event by throwing it right before
                    // login
                    this.$rootScope.$broadcast(TSAuthEvent[TSAuthEvent.LOGOUT_SUCCESS], 'logged out before logging in in');
                    return this.$timeout((): any => { // Response cookies are not immediately accessible, so lets wait for a bit
                        try {
                            this.initWithCookie();

                            return this.principal;
                        } catch (e) {
                            return this.$q.reject();
                        }
                    }, 100);

                });

        }
        return undefined;
    }

    public initWithCookie(): boolean {
        let authIdbase64 = this.$cookies.get('authId');
        if (authIdbase64) {
            try {
                let authData = angular.fromJson(this.base64.decode(authIdbase64));
                this.principal = new TSUser(authData.vorname, authData.nachname, authData.authId, '', authData.email, authData.mandant, authData.role);
                this.$timeout(() => {
                    this.$rootScope.$broadcast(TSAuthEvent[TSAuthEvent.LOGIN_SUCCESS], 'logged in');
                }); //bei login muessen wir warten bis angular alle componenten erstellt hat bevor wir das event werfen

                return true;
            } catch (e) {
                console.log('cookie decoding failed');
            }
        }

        return false;
    }

    public logoutRequest() {
        return this.$http.post(this.CONSTANTS.REST_API + 'auth/logout', null).then((res: any) => {
            this.principal = undefined;
            this.$rootScope.$broadcast(TSAuthEvent[TSAuthEvent.LOGOUT_SUCCESS], 'logged out');
            return res;
        });
    }

    public initSSOLogin(relayPath: string): IPromise<string> {
        return this.$http.get(this.CONSTANTS.REST_API + 'auth/singleSignOn', {params: {relayPath: relayPath}}).then((res: any) => {
            return res.data;
        });
    }

    public initSingleLogout(relayPath: string): IPromise<string> {
        return this.$http.get(this.CONSTANTS.REST_API + 'auth/singleLogout', {params: {relayPath: relayPath}}).then((res: any) => {
            return res.data;
        });
    }

    /**
     * Gibt true zurueck, wenn der eingelogte Benutzer die gegebene Role hat. Fuer undefined Werte wird immer false
     * zurueckgegeben.
     */
    public isRole(role: TSRole) {
        if (role && this.principal) {
            return this.principal.role === role;
        }
        return false;
    }

    /**
     * gibt true zurueck wenn der aktuelle Benutzer eine der uebergebenen Rollen innehat
     */
    public isOneOfRoles(roles: Array<TSRole>): boolean {
        if (roles !== undefined && roles !== null && this.principal) {
            for (let i = 0; i < roles.length; i++) {
                let role = roles[i];
                if (role === this.principal.role) {
                    return true;
                }
            }
        }
        return false;
    }
}
