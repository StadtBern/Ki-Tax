import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService} from 'angular';
import TSGesuchsteller from '../../models/TSGesuchsteller';

export default class GesuchstellerRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'gesuchsteller';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;

    }

    public updateGesuchsteller(gesuchsteller: TSGesuchsteller): IPromise<TSGesuchsteller> {
        let restPers = {};
        restPers = this.ebeguRestUtil.gesuchstellerToRestObject(restPers, gesuchsteller);

        return this.http.put(this.serviceURL, restPers, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
                this.log.debug('PARSING gesuchsteller REST object ', response.data);
                return this.ebeguRestUtil.parseGesuchsteller(new TSGesuchsteller(), response.data);
            }
        );
    }

    public create(gesuchsteller: TSGesuchsteller, gesuchId: string): IPromise<TSGesuchsteller> {
        let gessteller = {};
        gessteller = this.ebeguRestUtil.gesuchstellerToRestObject(gessteller, gesuchsteller);
        return this.http.post(this.serviceURL + '/' + gesuchId, gessteller, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING gesuchsteller REST object ', response.data);
            return this.ebeguRestUtil.parseGesuchsteller(new TSGesuchsteller(), response.data);
        });

    }

    public findGesuchsteller(gesuchstellerID: string): IPromise<TSGesuchsteller> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchstellerID))
            .then((response: any) => {
                this.log.debug('PARSING gesuchsteller REST object ', response.data);
                return this.ebeguRestUtil.parseGesuchsteller(new TSGesuchsteller(), response.data);
            });

    }

    public getServiceName(): string {
        return 'GesuchstellerRS';
    }

}

