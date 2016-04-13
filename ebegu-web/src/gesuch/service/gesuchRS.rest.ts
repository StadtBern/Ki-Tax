import {IEntityRS} from '../../core/service/iEntityRS.rest';
import {IHttpPromise, IHttpService} from 'angular';
import TSGesuch from '../../models/TSGesuch';
import EbeguRestUtil from '../../utils/EbeguRestUtil';

export default class GesuchRS implements IEntityRS {
    serviceURL:string;
    http:IHttpService;
    ebeguRestUtil:EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'ebeguRestUtil'];
    /* @ngInject */
    constructor($http:IHttpService, REST_API:string, ebeguRestUtil:EbeguRestUtil) {
        this.serviceURL = REST_API + 'gesuche';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public create(gesuch: TSGesuch): IHttpPromise<any> {
        let returnedGesuch = {};
        returnedGesuch = this.ebeguRestUtil.gesuchToRestObject(returnedGesuch,gesuch);
        return this.http.post(this.serviceURL, returnedGesuch, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public update(gesuch: TSGesuch): IHttpPromise<any> {
        let returnedGesuch = {};
        returnedGesuch = this.ebeguRestUtil.gesuchToRestObject(returnedGesuch,gesuch);
        return this.http.put(this.serviceURL,  returnedGesuch, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public findGesuch(gesuchID:string): IHttpPromise<any> {
        return this.http.get( this.serviceURL + '/' + encodeURIComponent(gesuchID));
    }

}
