import TSAdresse from '../../models/TSAdresse';
import {IHttpService, IHttpPromise, IPromise} from 'angular';
import TSGesuch from '../../models/TSGesuch';

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

    public updateUmzug(adresse: TSAdresse): IPromise<TSGesuch> {
        return this.http.put(this.serviceURL + '/umzug/', adresse, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }
}
