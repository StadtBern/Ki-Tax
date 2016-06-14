import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSEbeguParameter from '../../models/TSEbeguParameter';
import {IHttpService, IPromise} from 'angular';
import {TSEbeguParameterKey} from '../../models/enums/TSEbeguParameterKey';
import DateUtil from '../../utils/DateUtil';


export class EbeguParameterRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil) {
        this.serviceURL = REST_API + 'parameter';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public saveEbeguParameter(tsEbeguParameter: TSEbeguParameter): IPromise<TSEbeguParameter> {
        let restEbeguParameter = {};
        restEbeguParameter = this.ebeguRestUtil.ebeguParameterToRestObject(restEbeguParameter, tsEbeguParameter);
        return this.http.put(this.serviceURL, restEbeguParameter, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
                return this.ebeguRestUtil.parseEbeguParameter(new TSEbeguParameter(), response.data);
            }
        );
    }

    public getAllEbeguParameter(): IPromise<TSEbeguParameter[]> {
        return this.http.get(this.serviceURL + '/all').then(
            (response: any) => {
                return this.ebeguRestUtil.parseEbeguParameters(response.data);
            }
        );
    }

    public getAllEbeguParameterByDate(dateParam: moment.Moment): IPromise<TSEbeguParameter[]> {
        return this.http.get(this.serviceURL + '/date', {params: {date: DateUtil.momentToLocalDate(dateParam)}})
            .then((response: any) => {
                return this.ebeguRestUtil.parseEbeguParameters(response.data);
            });
    }

    public getEbeguParameterByGesuchsperiode(gesuchsperiodeId: string): IPromise<TSEbeguParameter[]> {
        return this.http.get(this.serviceURL + '/gesuchsperiode/' + gesuchsperiodeId)
            .then((response: any) => {
                return this.ebeguRestUtil.parseEbeguParameters(response.data);
            });
    }

    public getEbeguParameterByJahr(year: number): IPromise<TSEbeguParameter[]> {
        return this.http.get(this.serviceURL + '/year/' + year)
            .then((response: any) => {
                return this.ebeguRestUtil.parseEbeguParameters(response.data);
            });
    }

    public getEbeguParameterByKeyAndDate(dateParam: moment.Moment, keyParam: TSEbeguParameterKey): IPromise<TSEbeguParameter> {
        return this.http.get(this.serviceURL + '/name/' + keyParam)
            .then((param: TSEbeguParameter) => {
                return param;
            });
    }
}
