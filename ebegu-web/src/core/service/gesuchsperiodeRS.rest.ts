import {IHttpService, ILogService, IPromise, IHttpPromise, IQService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSGesuchsperiode from '../../models/TSGesuchsperiode';

export default class GesuchsperiodeRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    private activeGesuchsperiodenList: Array<TSGesuchsperiode>;
    private nichtAbgeschlosseneGesuchsperiodenList: Array<TSGesuchsperiode>;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', '$q'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService, private $q: IQService) {
        this.serviceURL = REST_API + 'gesuchsperioden';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'GesuchsperiodeRS';
    }

    public findGesuchsperiode(gesuchsperiodeID: string): IPromise<TSGesuchsperiode> {
        return this.http.get(this.serviceURL + '/gesuchsperiode/' + encodeURIComponent(gesuchsperiodeID))
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

    public updateActiveGesuchsperiodenList(): IPromise<TSGesuchsperiode[]> {
        return this.http.get(this.serviceURL + '/active').then((response: any) => {
            let gesuchsperioden: TSGesuchsperiode[] = this.ebeguRestUtil.parseGesuchsperioden(response.data);
            return this.activeGesuchsperiodenList = angular.copy(gesuchsperioden);
        });
    }

    public getAllActiveGesuchsperioden(): IPromise<TSGesuchsperiode[]> {
        if (!this.activeGesuchsperiodenList || this.activeGesuchsperiodenList.length <= 0) { // if the list is empty, reload it
            return this.updateActiveGesuchsperiodenList().then(() => {
                return this.activeGesuchsperiodenList;
            });
        }
        return this.$q.when(this.activeGesuchsperiodenList); // we need to return a promise
    }

    public getAllGesuchsperioden(): IPromise<TSGesuchsperiode[]> {
        return this.http.get(this.serviceURL + '/').then((response: any) => {
            return this.ebeguRestUtil.parseGesuchsperioden(response.data);
        });
    }

    public updateNichtAbgeschlosseneGesuchsperiodenList(): IPromise<TSGesuchsperiode[]> {
        return this.http.get(this.serviceURL + '/unclosed').then((response: any) => {
            let gesuchsperioden: TSGesuchsperiode[] = this.ebeguRestUtil.parseGesuchsperioden(response.data);
            return this.nichtAbgeschlosseneGesuchsperiodenList = angular.copy(gesuchsperioden);
        });
    }

    public getAllNichtAbgeschlosseneGesuchsperioden(): IPromise<TSGesuchsperiode[]> {
        if (!this.nichtAbgeschlosseneGesuchsperiodenList || this.nichtAbgeschlosseneGesuchsperiodenList.length <= 0) { // if the list is empty, reload it
            return this.updateNichtAbgeschlosseneGesuchsperiodenList().then(() => {
                return this.nichtAbgeschlosseneGesuchsperiodenList;
            });
        }
        return this.$q.when(this.nichtAbgeschlosseneGesuchsperiodenList); // we need to return a promise
    }
}
