import {IHttpService, ILogService, IPromise, IHttpPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSInstitutionStammdaten from '../../models/TSInstitutionStammdaten';
import DateUtil from '../../utils/DateUtil';

export class InstitutionStammdatenRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'institutionstammdaten';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public findInstitutionStammdaten(institutionStammdatenID: string): IPromise<TSInstitutionStammdaten> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(institutionStammdatenID))
            .then((response: any) => {
                this.log.debug('PARSING InstitutionStammdaten REST object ', response.data);
                return this.ebeguRestUtil.parseInstitutionStammdaten(new TSInstitutionStammdaten(), response.data);
            });
    }

    public createInstitutionStammdaten(institutionStammdaten: TSInstitutionStammdaten): IPromise<TSInstitutionStammdaten> {
        return this.saveInstitutionStammdaten(institutionStammdaten);
    }

    public updateInstitutionStammdaten(institutionStammdaten: TSInstitutionStammdaten): IPromise<TSInstitutionStammdaten> {
        return this.saveInstitutionStammdaten(institutionStammdaten);
    }

    private saveInstitutionStammdaten(institutionStammdaten: TSInstitutionStammdaten): IPromise<TSInstitutionStammdaten> {
        let restInstitutionStammdaten = {};
        restInstitutionStammdaten = this.ebeguRestUtil.institutionStammdatenToRestObject(restInstitutionStammdaten, institutionStammdaten);

        return this.http.put(this.serviceURL, restInstitutionStammdaten, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
                this.log.debug('PARSING institutionStammdaten REST object', response.data);
                return this.ebeguRestUtil.parseInstitutionStammdaten(new TSInstitutionStammdaten(), response.data);
            }
        );
    }

    public removeInstitutionStammdaten(institutionStammdatenID: string): IHttpPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(institutionStammdatenID));
    }

    public getAllInstitutionStammdaten(): IPromise<TSInstitutionStammdaten[]> {
        return this.http.get(this.serviceURL).then((response: any) => {
            this.log.debug('PARSING institutionStammdaten REST array object', response.data);
            return this.ebeguRestUtil.parseInstitutionStammdatenArray(response.data);
        });
    }

    public getAllInstitutionStammdatenByDate(dateParam: moment.Moment): IPromise<TSInstitutionStammdaten[]> {
        return this.http.get(this.serviceURL + '/date', {params: {date: DateUtil.momentToLocalDate(dateParam)}})
            .then((response: any) => {
                this.log.debug('PARSING institutionStammdaten REST array object', response.data);
                return this.ebeguRestUtil.parseInstitutionStammdatenArray(response.data);
            });
    }

    public getAllInstitutionStammdatenByInstitution(institutionID: string): IPromise<TSInstitutionStammdaten[]> {
        return this.http.get(this.serviceURL + '/institution' + '/' + encodeURIComponent(institutionID))
            .then((response: any) => {
                this.log.debug('PARSING institutionStammdaten REST array object', response.data);
                return this.ebeguRestUtil.parseInstitutionStammdatenArray(response.data);
            });
    }


    public getServiceName(): string {
        return 'InstitutionStammdatenRS';
    }

}
