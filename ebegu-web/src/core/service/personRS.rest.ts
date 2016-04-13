import TSPerson from '../../models/TSPerson';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IHttpPromise} from 'angular';

export default class PersonRS {
    serviceURL:string;
    http:IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API'];
    /* @ngInject */
    constructor($http:IHttpService, REST_API:string, ebeguRestUtil: EbeguRestUtil) {
        this.serviceURL = REST_API + 'personen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public update(person: TSPerson): IHttpPromise<any> {
        let pers = {};
        pers = this.ebeguRestUtil.personToRestObject(pers,person);

        return this.http.put(this.serviceURL,  pers, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public create(person: TSPerson): IHttpPromise<any> {
        let pers = {};
        pers = this.ebeguRestUtil.personToRestObject(pers,person);
        return this.http.post(this.serviceURL, pers, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public findPerson(personID: string): IHttpPromise<any> {
        return this.http.get( this.serviceURL + '/' + encodeURIComponent(personID));
    }

}

