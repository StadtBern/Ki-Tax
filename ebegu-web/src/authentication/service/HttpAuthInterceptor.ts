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

import {IHttpInterceptor, IQService, IRootScopeService} from 'angular';
import {TSAuthEvent} from '../../models/enums/TSAuthEvent';
import HttpBuffer from './HttpBuffer';

export default class HttpAuthInterceptor implements IHttpInterceptor {

    static $inject = ['$rootScope', '$q', 'CONSTANTS', 'httpBuffer'];
    /* @ngInject */
    constructor(private $rootScope: IRootScopeService, private $q: IQService, private CONSTANTS: any,
                private httpBuffer: HttpBuffer) {
    }


    public responseError = (response: any) => {
        switch (response.status) {
            case 401:
                // exclude requests from the login form
                if (response.config && response.config.url === this.CONSTANTS.REST_API + 'auth/login') {
                    return this.$q.reject(response);
                }
                //if this request was a background polling request we do not want to relogin or show errors
                if (response.config && response.config.url.indexOf('notokenrefresh') > 0) {
                    console.debug('rejecting failed notokenrefresh response');
                    return this.$q.reject(response);
                }
                // all requests that failed due to notAuthenticated are appended to httpBuffer. Use httpBuffer.retryAll to submit them.
                let deferred = this.$q.defer();
                this.httpBuffer.append(response.config, deferred);
                this.$rootScope.$broadcast(TSAuthEvent[TSAuthEvent.NOT_AUTHENTICATED], response);
                return deferred.promise;
            case 403:
                this.$rootScope.$broadcast(TSAuthEvent[TSAuthEvent.NOT_AUTHORISED], response);
                return this.$q.reject(response);
        }
        return this.$q.reject(response);
    }
}
