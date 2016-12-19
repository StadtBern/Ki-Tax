import {IEntityRS} from '../../core/service/iEntityRS.rest';
import {IHttpService, IPromise, ILogService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSUser from '../../models/TSUser';

export default class UserRS implements IEntityRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private $log: ILogService) {
        this.serviceURL = REST_API + 'benutzer';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public getAllUsers(): IPromise<TSUser[]> {
        return this.http.get(this.serviceURL).then((response: any) => {
            this.$log.debug('PARSING user REST array object', response.data);
            return this.ebeguRestUtil.parseUserList(response.data);
        });
    }

    public getBenutzerJAorAdmin(): IPromise<TSUser[]> {
        return this.http.get(this.serviceURL + '/JAorAdmin').then((response: any) => {
            this.$log.debug('PARSING user REST array object', response.data);
            return this.ebeguRestUtil.parseUserList(response.data);
        });
    }

    public getServiceName(): string {
        return 'UserRS';
    }

    public getAllGesuchsteller(): IPromise<TSUser[]> {
        return this.http.get(this.serviceURL + '/gesuchsteller').then((response: any) => {
            this.$log.debug('PARSING user REST array object', response.data);
            return this.ebeguRestUtil.parseUserList(response.data);
        });
    }

}
