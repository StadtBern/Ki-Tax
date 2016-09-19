import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, ILogService, IPromise} from 'angular';
import TSAntragStatusHistory from '../../models/TSAntragStatusHistory';

export default class AntragStatusHistoryRS {

    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'antragStatusHistory';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'AntragStatusHistoryRS';
    }

    public findLastStatusChange(gesuchId: string): IPromise<TSAntragStatusHistory> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchId))
            .then((response: any) => {
                this.log.debug('PARSING AntragStatusHistory REST object ', response.data);
                return this.ebeguRestUtil.parseAntragStatusHistory(new TSAntragStatusHistory(), response.data);
            });
    }

}
