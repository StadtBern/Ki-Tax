import {IQService}  from 'angular';
import IRootScopeService = angular.IRootScopeService;
import IHttpInterceptor = angular.IHttpInterceptor;
import {TSAuthEvent} from '../../models/enums/TSAuthEvent';

export default class HttpAuthInterceptor implements IHttpInterceptor {

    static $inject = ['$rootScope', '$q', 'CONSTANTS', '$window'];
    /* @ngInject */
    constructor(private $rootScope: IRootScopeService, private $q: IQService, private CONSTANTS: any, private $window: any) {
    }


    public responseError = (response: any) => {
        switch (response.status) {
            case 401:
                // exclude requests from the login form
                if (response.config && response.config.url === this.CONSTANTS.REST_API + '/api/v1/auth/login') {
                    return this.$q.reject(response);
                }
                // all requests that failed due to notAuthenticated are appsended to httpBuffer. Use httpBuffer.retryAll to submit them.
                // this.httpBuffer.append(response.config, deferred);
                this.$rootScope.$broadcast(TSAuthEvent[TSAuthEvent.NOT_AUTHENTICATED], response);
                this.$window.location.href = '/#/src/authentication/dummyAuthentication.html';
                return this.$q.defer().promise;
        }
        return this.$q.reject(response);
    };
}
