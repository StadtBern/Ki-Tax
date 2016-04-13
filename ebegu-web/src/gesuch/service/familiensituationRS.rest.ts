import {IHttpPromise, IHttpService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSFamiliensituation from '../../models/TSFamiliensituation';


export default class FamiliensituationRS {
    serviceURL:string;
    http:IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'ebeguRestUtil'];
    /* @ngInject */
    constructor($http:IHttpService, REST_API:string, ebeguRestUtil: EbeguRestUtil) {
        this.serviceURL = REST_API + 'familiensituation';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public create(familiensituation: TSFamiliensituation): IHttpPromise<any> {
        let returnedFamiliensituation = {};
        returnedFamiliensituation = this.ebeguRestUtil.familiensituationToRestObject(returnedFamiliensituation,familiensituation);
        return this.http.post(this.serviceURL, returnedFamiliensituation, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public update(familiensituation: TSFamiliensituation): IHttpPromise<any> {
        let returnedFamiliensituation = {};
        returnedFamiliensituation = this.ebeguRestUtil.familiensituationToRestObject(returnedFamiliensituation,familiensituation);
        return this.http.put(this.serviceURL,  returnedFamiliensituation, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public findFamiliensituation(familiensituationID:string): IHttpPromise<any> {
        return this.http.get( this.serviceURL + '/' + encodeURIComponent(familiensituationID));
    }

}
