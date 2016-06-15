import {IPromise, IHttpService, IQService, ITimeoutService} from 'angular';
import TSUser from '../../models/TSUser';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import ICookiesService = angular.cookies.ICookiesService;

export default class AuthServiceRS {

    private principal: TSUser = new TSUser();


    static $inject = ['$http', 'CONSTANTS', '$q', '$timeout', '$cookies', '$base64', 'EbeguRestUtil'];
    /* @ngInject */
    constructor(private $http: IHttpService, private CONSTANTS: any, private $q: IQService, private $timeout: ITimeoutService,
                private $cookies: ICookiesService, private $base64: any, private ebeguRestUtil: EbeguRestUtil) {
    }

    public loginRequest(userCredentials: TSUser): IPromise<TSUser> {
        return this.$http.post(this.CONSTANTS.REST_API + 'auth/login', this.ebeguRestUtil.userToRestObject({}, userCredentials))
            .then((response: any) => {
            // try to reload buffered requests
            // httpBuffer.retryAll(function (config) {
            //     return config;
            // });
            return this.$timeout((): any => { // Response cookies are not immediately accessible, so lets wait for a bit
                try {
                    this.initWithCookie();
                    return this.principal;
                } catch (e) {
                    return this.$q.reject();
                }
            }, 100);
        });
    };

    public initWithCookie(): boolean {
        var authIdbase64 = this.$cookies.get('authId');
        if (authIdbase64) {
            try {
                var authData = angular.fromJson(this.$base64.decode(authIdbase64));
                this.principal.username = authData.authId;
                this.principal.userId = authData.userId;
                if (authData.roles) {
                    this.principal.roles = authData.roles;
                }
                return true;
            } catch (e) {
                console.log('cookie decoding failed');
            }
        }

        return false;
    };

}
