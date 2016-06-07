import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService} from 'angular';
import TSPendenzJA from '../../models/TSPendenzJA';

export default class PendenzRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'pendenzen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'PendenzRS';
    }

    public getPendenzenList(): IPromise<Array<TSPendenzJA>> {
        return this.http.get(this.serviceURL)
            .then((response: any) => {
                this.log.debug('PARSING pendenz REST object ', response.data);
                return this.ebeguRestUtil.parsePendenzen(response.data);
            });
    }

}
