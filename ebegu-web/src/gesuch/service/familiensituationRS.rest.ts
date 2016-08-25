import {IHttpPromise, IHttpService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSFamiliensituation from '../../models/TSFamiliensituation';


export default class FamiliensituationRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil) {
        this.serviceURL = REST_API + 'familiensituation';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public create(familiensituation: TSFamiliensituation, gesuchId: string): IHttpPromise<any> {
        let returnedFamiliensituation = {};
        returnedFamiliensituation = this.ebeguRestUtil.familiensituationToRestObject(returnedFamiliensituation, familiensituation);
        return this.http.post(this.serviceURL + '/' + gesuchId, returnedFamiliensituation, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public update(familiensituation: TSFamiliensituation, gesuchId: string): IHttpPromise<any> {
        let returnedFamiliensituation = {};
        returnedFamiliensituation = this.ebeguRestUtil.familiensituationToRestObject(returnedFamiliensituation, familiensituation);
        return this.http.put(this.serviceURL + '/' + gesuchId, returnedFamiliensituation, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

}
