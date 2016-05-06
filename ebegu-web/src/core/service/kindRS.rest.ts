import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService, IHttpPromise} from 'angular';
import TSKindContainer from '../../models/TSKindContainer';

export default class KindRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'kinder';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'KindRS';
    }

    public findKind(kindContainerID: string): IPromise<TSKindContainer> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(kindContainerID))
            .then((response: any) => {
                this.log.debug('PARSING kindContainer REST object ', response.data);
                return this.ebeguRestUtil.parseKindContainer(new TSKindContainer(), response.data);
            });
    }

    public createKind(kindContainer: TSKindContainer, gesuchId: string): IPromise<TSKindContainer> {
        return this.saveKind(kindContainer, gesuchId);
    }

    public updateKind(kindContainer: TSKindContainer, gesuchId: string): IPromise<TSKindContainer> {
        return this.saveKind(kindContainer, gesuchId);
    }

    private saveKind(kindContainer: TSKindContainer, gesuchId: string): IPromise<TSKindContainer> {
        let restKind = {};
        restKind = this.ebeguRestUtil.kindContainerToRestObject(restKind, kindContainer);
        return this.http.put(this.serviceURL + '/' + gesuchId, restKind, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING KindContainer REST object ', response.data);
            return this.ebeguRestUtil.parseKindContainer(new TSKindContainer(), response.data);
        });
    }

    public removeKind(kindID: string): IHttpPromise<TSKindContainer> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(kindID));
    }
}
