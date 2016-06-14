import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService} from 'angular';
import {TSMandant} from '../../models/TSMandant';

export class MandantRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'mandanten';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public findMandant(mandantID: string): IPromise<TSMandant> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(mandantID))
            .then((response: any) => {
                this.log.debug('PARSING mandant REST object ', response.data);
                return this.ebeguRestUtil.parseMandant(new TSMandant(), response.data);
            });
    }

    /**
     * laedt und cached den ersten und einzigenMandanten aus der DB
     * @returns {IPromise<TSMandant>}
     */
    public getFirst(): IPromise<TSMandant> {
        return this.http.get(this.serviceURL + '/first', { cache: true })
            .then((response: any) => {
                this.log.debug('PARSING mandant REST object ', response.data);
                return this.ebeguRestUtil.parseMandant(new TSMandant(), response.data);
            });
    }

    public getServiceName(): string {
        return 'MandantRS';
    }

}
