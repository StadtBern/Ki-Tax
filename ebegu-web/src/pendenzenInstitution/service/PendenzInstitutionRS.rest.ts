import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService} from 'angular';
import TSPendenzInstitution from '../../models/TSPendenzInstitution';

export default class PendenzInstitutionRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, private REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'pendenzen/institution';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'PendenzInstitutionRS';
    }

    public getPendenzenList(): IPromise<Array<TSPendenzInstitution>> {
        return this.http.get(this.serviceURL)
            .then((response: any) => {
                this.log.debug('PARSING pendenzenInstitution REST object ', response.data);
                return this.ebeguRestUtil.parsePendenzenInstitution(response.data);
            });
    }
}
