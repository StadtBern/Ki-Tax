import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpResponse, IHttpService, ILogService} from 'angular';
import TSQuickSearchResult from '../../models/dto/TSQuickSearchResult';
import IPromise = angular.IPromise;


export class SearchIndexRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private $log: ILogService) {
        this.serviceURL = REST_API + 'search/';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    /**
     * performs a global search that will only return a certain ammount of results
     * @param query searchstring
     * @returns {IPromise<TSQuickSearchResult>}
     */
    quickSearch(query: string): IPromise<TSQuickSearchResult> {
        return this.http.get(this.serviceURL + 'quicksearch' + '/' + query).then((response: IHttpResponse<TSQuickSearchResult>) => {
            return this.ebeguRestUtil.parseQuickSearchResult(response.data);
        });
    }


    /**
     * performs a global search that will return the full number of matched results
     * @param query searchstring
     * @returns {IPromise<TSQuickSearchResult>}
     */
    globalSearch(query: string): IPromise<TSQuickSearchResult> {
        return this.http.get(this.serviceURL + 'globalsearch' + '/' + query).then((response: IHttpResponse<TSQuickSearchResult>) => {
            return this.ebeguRestUtil.parseQuickSearchResult(response.data);
        });
    }
}

