import {IHttpInterceptor, IRootScopeService, IQService} from 'angular';
import {TSHTTPEvent} from '../events/TSHTTPEvent';

/**
 * this interceptor boradcasts a REQUEST_FINISHED event whenever a rest service responds
 */
export default class HttpResponseInterceptor implements IHttpInterceptor {

    static $inject = ['$rootScope', '$q'];
    /* @ngInject */
    constructor(private $rootScope: IRootScopeService, private $q: IQService) {
    }


    public responseError = (response: any) => {
        this.$rootScope.$broadcast(TSHTTPEvent[TSHTTPEvent.REQUEST_FINISHED], response);
        return this.$q.reject(response);
    }

    public response = (response: any) => {
        this.$rootScope.$broadcast(TSHTTPEvent[TSHTTPEvent.REQUEST_FINISHED], response);
        return response;
    }

}
