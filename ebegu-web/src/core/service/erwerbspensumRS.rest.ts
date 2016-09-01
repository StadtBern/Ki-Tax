import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService, IHttpPromise} from 'angular';
import TSErwerbspensumContainer from '../../models/TSErwerbspensumContainer';

export default class ErwerbspensumRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'erwerbspensen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'ErwerbspensumRS';
    }

    public findErwerbspensum(erwerbspensenContainerID: string): IPromise<TSErwerbspensumContainer> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(erwerbspensenContainerID))
            .then((response: any) => {
                this.log.debug('PARSING erwerbspensenContainer REST object ', response.data);
                return this.ebeguRestUtil.parseErwerbspensumContainer(new TSErwerbspensumContainer(), response.data);
            });
    }

    public createErwerbspensum(erwerbspensenContainer: TSErwerbspensumContainer, gesuchstellerID: string, gesuchId: string): IPromise<TSErwerbspensumContainer> {
        return this.saveErwerbspensum(erwerbspensenContainer, gesuchstellerID, gesuchId);
    }

    public updateErwerbspensum(erwerbspensenContainer: TSErwerbspensumContainer, gesuchstellerID: string, gesuchId: string): IPromise<TSErwerbspensumContainer> {
        return this.saveErwerbspensum(erwerbspensenContainer, gesuchstellerID, gesuchId);
    }

    private saveErwerbspensum(erwerbspensenContainer: TSErwerbspensumContainer, gesuchstellerID: string, gesuchId: string): IPromise<TSErwerbspensumContainer> {
        let restErwerbspensum = {};
        restErwerbspensum = this.ebeguRestUtil.erwerbspensumContainerToRestObject(restErwerbspensum, erwerbspensenContainer);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(gesuchstellerID) + '/' + gesuchId, restErwerbspensum, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING ErwerbspensumContainer REST object ', response.data);
            return this.ebeguRestUtil.parseErwerbspensumContainer(new TSErwerbspensumContainer(), response.data);
        });
    }

    public removeErwerbspensum(erwerbspensumContID: string, gesuchId: string): IHttpPromise<TSErwerbspensumContainer> {
        return this.http.delete(this.serviceURL + '/gesuchId/' + encodeURIComponent(gesuchId) + '/erwPenId/' + encodeURIComponent(erwerbspensumContID));
    }
}
