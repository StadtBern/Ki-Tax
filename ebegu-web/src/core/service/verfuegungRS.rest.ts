import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService} from 'angular';
import TSGesuch from '../../models/TSGesuch';
import TSKindContainer from '../../models/TSKindContainer';

export default class VerfuegungRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'verfuegung';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'VerfuegungRS';
    }

    public calculateVerfuegung(gesuchID: string): IPromise<TSKindContainer[]> {
        return this.http.get(this.serviceURL + '/calculate/' + encodeURIComponent(gesuchID))
            .then((response: any) => {
                this.log.debug('PARSING KindContainers REST object ', response.data);
                return this.ebeguRestUtil.parseKindContainerList(response.data);
            });
    }
}
