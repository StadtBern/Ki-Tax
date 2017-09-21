import {IHttpInterceptor, IHttpResponse, IPromise, IQService, IRootScopeService} from 'angular';
import {TSAuthEvent} from '../../models/enums/TSAuthEvent';
import HttpBuffer from './HttpBuffer';

export default class HttpAuthInterceptor implements IHttpInterceptor {

    static $inject = ['$rootScope', '$q', 'CONSTANTS', 'httpBuffer'];

    /* @ngInject */
    constructor(private $rootScope: IRootScopeService, private $q: IQService, private CONSTANTS: any,
                private httpBuffer: HttpBuffer) {
    }

    responseError<T>(response: any): IPromise<IHttpResponse<T>> | IHttpResponse<T> {
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
                // all requests that failed due to notAuthenticated are appended to httpBuffer. Use httpBuffer.retryAll
                // to submit them.
                let deferred = this.$q.defer();
                this.httpBuffer.append(response.config, deferred);
                this.$rootScope.$broadcast(TSAuthEvent[TSAuthEvent.NOT_AUTHENTICATED], response);
                return deferred.promise as IPromise<IHttpResponse<T>>;
            case 403:
                this.$rootScope.$broadcast(TSAuthEvent[TSAuthEvent.NOT_AUTHORISED], response);
                return this.$q.reject(response);
        }
        return this.$q.reject(response);
    }
}
