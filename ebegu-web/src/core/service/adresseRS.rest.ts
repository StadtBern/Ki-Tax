import TSAdresse from '../../models/TSAdresse';
import {IHttpService, IHttpPromise} from 'angular';

export default class AdresseRS {
    static $inject = ['$http', 'REST_API'];

    serviceURL: string;
    http: IHttpService;

    /* @ngInject */
    constructor($http: IHttpService, REST_API: string) {
        this.serviceURL = REST_API + 'adressen';
        this.http = $http;
    }

    public create(adresse: TSAdresse): IHttpPromise<any> {
        return this.http.post(this.serviceURL, adresse, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    static instance ($http: IHttpService, REST_API: string) : AdresseRS {
        return new AdresseRS($http, REST_API);
    }
}
