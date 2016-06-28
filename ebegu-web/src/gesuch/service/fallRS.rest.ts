import {IHttpPromise, IHttpService, ILogService, IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSFall from '../../models/TSFall';

export default class FallRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private $log: ILogService) {
        this.serviceURL = REST_API + 'falle';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public createFall(fall: TSFall): IHttpPromise<any> {
        let returnedFall = {};
        returnedFall = this.ebeguRestUtil.fallToRestObject(returnedFall, fall);
        return this.http.post(this.serviceURL, returnedFall, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public updateFall(fall: TSFall): IHttpPromise<any> {
        let returnedFall = {};
        returnedFall = this.ebeguRestUtil.fallToRestObject(returnedFall, fall);
        return this.http.put(this.serviceURL, returnedFall, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public findFall(fallID: string): IPromise<any> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(fallID))
            .then((response: any) => {
                this.$log.debug('PARSING fall REST object ', response.data);
                return this.ebeguRestUtil.parseFall(new TSFall(), response.data);
            });
    }

}
