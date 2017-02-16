import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService} from 'angular';
import TSZahlung from '../../models/TSZahlung';
import TSZahlungsauftrag from '../../models/TSZahlungsauftrag';

export default class ZahlungRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private  $log: ILogService) {
        this.serviceURL = REST_API + 'zahlungen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'ZahlungRS';
    }

    public getAllZahlungsauftraege(): IPromise<TSZahlungsauftrag[]> {
        return this.http.get(this.serviceURL+ '/all').then((response: any) => {
            this.$log.debug('PARSING user REST array object', response.data);
            return this.ebeguRestUtil.parseZahlungsauftragList(response.data);
        });
    }

    public createZahlung(): IPromise<TSZahlungsauftrag> {
        return this.http.post(this.serviceURL+ '/create', {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((httpresponse: any) => {
            this.log.debug('PARSING Zahlungsauftrag REST object ', httpresponse.data);
            return this.ebeguRestUtil.parseZahlungsauftrag(new TSZahlungsauftrag(), httpresponse.data);
        });
    }

}
