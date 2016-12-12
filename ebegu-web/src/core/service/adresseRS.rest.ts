import {IHttpService, IHttpPromise} from 'angular';
import TSAdresseContainer from '../../models/TSAdresseContainer';

export default class AdresseRS {
    static $inject = ['$http', 'REST_API'];

    serviceURL: string;
    http: IHttpService;

    /* @ngInject */
    constructor($http: IHttpService, REST_API: string) {
        this.serviceURL = REST_API + 'adressen';
        this.http = $http;
    }

    public create(adresse: TSAdresseContainer): IHttpPromise<any> {
        return this.http.post(this.serviceURL, adresse, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

}
