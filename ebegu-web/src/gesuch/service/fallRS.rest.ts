import {IHttpService, ILogService, IPromise} from 'angular';
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

    public createFall(fall: TSFall): IPromise<any> {
        return this.saveFall(fall);
    }

    public updateFall(fall: TSFall): IPromise<any> {
        return this.saveFall(fall);
    }

    private saveFall(fall: TSFall): IPromise<TSFall> {
        let fallObject = {};
        fallObject = this.ebeguRestUtil.fallToRestObject(fallObject, fall);

        return this.http.put(this.serviceURL, fallObject, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.$log.debug('PARSING fall REST object ', response.data);
            this.$log.debug('PARSed fall REST object ', this.ebeguRestUtil.parseFall(new TSFall(), response.data));
            return this.ebeguRestUtil.parseFall(new TSFall(), response.data);
        });
    }

    public findFall(fallID: string): IPromise<any> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(fallID))
            .then((response: any) => {
                this.$log.debug('PARSING fall REST object ', response.data);
                return this.ebeguRestUtil.parseFall(new TSFall(), response.data);
            });
    }

    public getServiceName(): string {
        return 'FallRS';
    }

}
