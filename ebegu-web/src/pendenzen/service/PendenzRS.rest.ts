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
    constructor($http: IHttpService, private REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'pendenzen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'PendenzRS';
    }

    public getPendenzenList(): IPromise<Array<TSPendenzJA>> {
        this.dotest();
        return this.http.get(this.serviceURL)
            .then((response: any) => {
                this.log.debug('PARSING pendenz REST object ', response.data);
                return this.ebeguRestUtil.parsePendenzen(response.data);
            });
    }

    public dotest() {
        console.log("testing mandant call")
        return this.http.get(this.REST_API + 'verfuegung/calculate/cdef5c90-eac5-41eb-af68-907bbf94d665').then((response: any) => {
            this.log.debug('PARSING verfuegung REST object ', response.data);

        });
    }

}
