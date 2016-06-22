import {IRequestConfig, IPromise, IHttpService, IQService, ITimeoutService} from 'angular';
import TSUser from '../../models/TSUser';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import ICookiesService = angular.cookies.ICookiesService;
import HttpBuffer from './HttpBuffer';

export default class AuthServiceRS {

    private principal: TSUser;


    static $inject = ['$http', 'CONSTANTS', '$q', '$timeout', '$cookies', 'base64', 'EbeguRestUtil', 'httpBuffer'];
    /* @ngInject */
    constructor(private $http: IHttpService, private CONSTANTS: any, private $q: IQService, private $timeout: ITimeoutService,
                private $cookies: ICookiesService, private base64: any, private ebeguRestUtil: EbeguRestUtil, private httpBuffer: HttpBuffer) {
        this.initWithCookie();
    }

    public getPrincipal() {
        return this.principal;
    }

    public loginRequest(userCredentials: TSUser): IPromise<TSUser> {
        if (userCredentials) {
            return this.$http.post(this.CONSTANTS.REST_API + 'auth/login', this.ebeguRestUtil.userToRestObject({}, userCredentials))
                .then((response: any) => {
                    // try to reload buffered requests
                    this.httpBuffer.retryAll((config: IRequestConfig) => {
                        return config;
                    });
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
    };

    private initWithCookie(): boolean {
        let authIdbase64 = this.$cookies.get('authId');
        console.log('from cookieas', authIdbase64);
        if (authIdbase64) {
            try {
                let authData = angular.fromJson(this.base64.decode(authIdbase64));
                console.log('real data', authData);
                this.principal = new TSUser(authData.vorname, authData.nachname, authData.authId, '', authData.email, authData.mandant, authData.role);
                return true;
            } catch (e) {
                console.log('cookie decoding failed');
            }
        }

        return false;
    };

    public logoutRequest() {
        return this.$http.post(this.CONSTANTS.REST_API + 'auth/logout', null).then((res: any) => {
            this.principal = undefined;
            return res;
        });
    };

}
