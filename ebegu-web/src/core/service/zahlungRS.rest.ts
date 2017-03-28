import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService} from 'angular';
import TSZahlungsauftrag from '../../models/TSZahlungsauftrag';
import DateUtil from '../../utils/DateUtil';
import TSZahlung from '../../models/TSZahlung';

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
        return this.http.get(this.serviceURL + '/all').then((response: any) => {
            this.$log.debug('PARSING user REST array object', response.data);
            return this.ebeguRestUtil.parseZahlungsauftragList(response.data);
        });
    }

    public getAllZahlungsauftraegeInstitution(): IPromise<TSZahlungsauftrag[]> {
        return this.http.get(this.serviceURL + '/institution').then((response: any) => {
            this.$log.debug('PARSING user REST array object', response.data);
            return this.ebeguRestUtil.parseZahlungsauftragList(response.data);
        });
    }

    public getZahlungsauftrag(zahlungsauftragId: string): IPromise<TSZahlungsauftrag> {
        return this.http.get(this.serviceURL + '/zahlungsauftrag' + '/' + encodeURIComponent(zahlungsauftragId)).then((response: any) => {
            this.$log.debug('PARSING user REST array object', response.data);
            return this.ebeguRestUtil.parseZahlungsauftrag(new TSZahlungsauftrag(), response.data);
        });
    }

    public getZahlungsauftragInstitution(zahlungsauftragId: string): IPromise<TSZahlungsauftrag> {
        return this.http.get(this.serviceURL + '/zahlungsauftraginstitution' + '/' + encodeURIComponent(zahlungsauftragId)).then((response: any) => {
            this.$log.debug('PARSING user REST array object', response.data);
            return this.ebeguRestUtil.parseZahlungsauftrag(new TSZahlungsauftrag(), response.data);
        });
    }

    public zahlungsauftragAusloesen(zahlungsauftragId: string): IPromise<TSZahlungsauftrag> {
        return this.http.put(this.serviceURL + '/ausloesen' + '/' + encodeURIComponent(zahlungsauftragId), null).then((response: any) => {
            this.$log.debug('PARSING user REST array object', response.data);
            return this.ebeguRestUtil.parseZahlungsauftrag(new TSZahlungsauftrag(), response.data);
        });
    }

    public zahlungBestaetigen(zahlungId: string): IPromise<TSZahlung> {
        return this.http.put(this.serviceURL + '/bestaetigen' + '/' + encodeURIComponent(zahlungId), null).then((response: any) => {
            this.$log.debug('PARSING user REST array object', response.data);
            return this.ebeguRestUtil.parseZahlung(new TSZahlung(), response.data);
        });
    }

    public createZahlungsauftrag(beschrieb: string, faelligkeitsdatum: moment.Moment, datumGeneriert: moment.Moment): IPromise<TSZahlungsauftrag> {
        return this.http.get(this.serviceURL + '/create',
            {
                params: {
                    faelligkeitsdatum: DateUtil.momentToLocalDate(faelligkeitsdatum),
                    beschrieb: beschrieb,
                    datumGeneriert: DateUtil.momentToLocalDate(datumGeneriert)
                }
            }).then((httpresponse: any) => {
            this.log.debug('PARSING Zahlungsauftrag REST object ', httpresponse.data);
            return this.ebeguRestUtil.parseZahlungsauftrag(new TSZahlungsauftrag(), httpresponse.data);
        });
    }

    public updateZahlungsauftrag(beschrieb: string, faelligkeitsdatum: moment.Moment, id: string): IPromise<TSZahlungsauftrag> {
        return this.http.get(this.serviceURL + '/update',
            {
                params: {
                    beschrieb: beschrieb,
                    faelligkeitsdatum: DateUtil.momentToLocalDate(faelligkeitsdatum),
                    id: id
                }
            }).then((httpresponse: any) => {
            this.log.debug('PARSING Zahlungsauftrag REST object ', httpresponse.data);
            return this.ebeguRestUtil.parseZahlungsauftrag(new TSZahlungsauftrag(), httpresponse.data);
        });
    }

}
