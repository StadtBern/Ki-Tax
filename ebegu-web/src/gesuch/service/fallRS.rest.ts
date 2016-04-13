import {IHttpPromise, IHttpService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSFall from '../../models/TSFall';

export default class FallRS {
    serviceURL:string;
    http:IHttpService;
    ebeguRestUtil:EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'ebeguRestUtil'];
    /* @ngInject */
    constructor($http:IHttpService, REST_API:string, ebeguRestUtil:EbeguRestUtil) {
        this.serviceURL = REST_API + 'falle';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public create(fall: TSFall): IHttpPromise<any> {
        let returnedFall = {};
        returnedFall = this.ebeguRestUtil.fallToRestObject(returnedFall,fall);
        return this.http.post(this.serviceURL, returnedFall, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public update(fall: TSFall): IHttpPromise<any> {
        let returnedFall = {};
        returnedFall = this.ebeguRestUtil.fallToRestObject(returnedFall,fall);
        return this.http.put(this.serviceURL,  returnedFall, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public findFall(fallID:string): IHttpPromise<any> {
        return this.http.get( this.serviceURL + '/' + encodeURIComponent(fallID));
    }

}
