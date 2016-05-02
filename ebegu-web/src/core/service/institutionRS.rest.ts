import {IHttpService, ILogService, IPromise, IHttpPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {TSInstitution} from '../../models/TSInstitution';

export class InstitutionRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'institutionen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public findInstitution(institutionID: string): IPromise<TSInstitution> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(institutionID))
            .then((response: any) => {
                this.log.debug('PARSING institution REST object ', response.data);
                return this.ebeguRestUtil.parseInstitution(new TSInstitution(), response.data);
            });
    }

    public createInstitution(institution: TSInstitution): IPromise<TSInstitution> {
        return this.saveInstitution(institution);
    }

    public updateInstitution(institution: TSInstitution): IPromise<TSInstitution> {
        return this.saveInstitution(institution);
    }

    private saveInstitution(institution: TSInstitution): IPromise<TSInstitution> {
        let restInstitution = {};
        restInstitution = this.ebeguRestUtil.institutionToRestObject(restInstitution, institution);
        return this.http.put(this.serviceURL, restInstitution, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING institution REST object ', response.data);
            return this.ebeguRestUtil.parseInstitution(new TSInstitution(), response.data);
        });
    }

    public removeInstitution(institutionID: string): IHttpPromise<TSInstitution> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(institutionID));
    }

    public getAllInstitutionen(): IPromise<TSInstitution[]> {
        return this.http.get(this.serviceURL).then((response: any) => {
            this.log.debug('PARSING institutionen REST array object', response.data);
            return this.ebeguRestUtil.parseInstitutionen(response.data);
        });
    }

    public getServiceName(): string {
        return 'InstitutionRS';
    }
}
