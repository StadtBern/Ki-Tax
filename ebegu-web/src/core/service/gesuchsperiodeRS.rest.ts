import {IHttpService, ILogService, IPromise, IHttpPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSGesuchsperiode from '../../models/TSGesuchsperiode';

export default class GesuchsperiodeRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'gesuchsperioden';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'GesuchsperiodeRS';
    }

    public findGesuchsperiode(gesuchsperiodeID: string): IPromise<TSGesuchsperiode> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchsperiodeID))
            .then((response: any) => {
                this.log.debug('PARSING Gesuchsperiode REST object ', response.data);
                return this.ebeguRestUtil.parseGesuchsperiode(new TSGesuchsperiode(), response.data);
            });
    }

    public createGesuchsperiode(gesuchsperiode: TSGesuchsperiode): IPromise<TSGesuchsperiode> {
        return this.saveGesuchsperiode(gesuchsperiode);
    }

    public updateGesuchsperiode(gesuchsperiode: TSGesuchsperiode): IPromise<TSGesuchsperiode> {
        return this.saveGesuchsperiode(gesuchsperiode);
    }

    private saveGesuchsperiode(gesuchsperiode: TSGesuchsperiode): IPromise<TSGesuchsperiode> {
        let restGesuchsperiode = {};
        restGesuchsperiode = this.ebeguRestUtil.gesuchsperiodeToRestObject(restGesuchsperiode, gesuchsperiode);
        return this.http.put(this.serviceURL, restGesuchsperiode, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING Gesuchsperiode REST object ', response.data);
            return this.ebeguRestUtil.parseGesuchsperiode(new TSGesuchsperiode(), response.data);
        });
    }

    public removeGesuchsperiode(gesuchsperiodeId: string): IHttpPromise<TSGesuchsperiode> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(gesuchsperiodeId));
    }

    public getAllActiveGesuchsperioden(): IPromise<TSGesuchsperiode[]> {
        return this.http.get(this.serviceURL + '/active').then((response: any) => {
            return this.ebeguRestUtil.parseGesuchsperioden(response.data);
        });
    }
}
