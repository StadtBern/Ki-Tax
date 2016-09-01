import {IHttpService, ILogService, IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSBetreuung from '../../models/TSBetreuung';

export default class BetreuungRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'betreuungen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'BetreuungRS';
    }

    public findBetreuung(betreuungID: string): IPromise<TSBetreuung> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(betreuungID))
            .then((response: any) => {
                this.log.debug('PARSING betreuung REST object ', response.data);
                return this.ebeguRestUtil.parseBetreuung(new TSBetreuung(), response.data);
            });
    }

    public createBetreuung(betreuung: TSBetreuung, kindId: string): IPromise<TSBetreuung> {
        return this.saveBetreuung(betreuung, kindId);
    }

    public updateBetreuung(betreuung: TSBetreuung, kindId: string): IPromise<TSBetreuung> {
        return this.saveBetreuung(betreuung, kindId);
    }

    private saveBetreuung(betreuung: TSBetreuung, kindId: string): IPromise<TSBetreuung> {
        let restBetreuung = {};
        restBetreuung = this.ebeguRestUtil.betreuungToRestObject(restBetreuung, betreuung);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(kindId), restBetreuung, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING Betreuung REST object ', response.data);
            return this.ebeguRestUtil.parseBetreuung(new TSBetreuung(), response.data);
        });
    }

    removeBetreuung(betreuungId: string) {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(betreuungId));
    }
}
