import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IHttpPromise, IPromise} from 'angular';
import TSPerson from '../../models/TSPerson';
import ILogService = angular.ILogService;

export default class PersonRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'personen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;

    }

    public update(person: TSPerson): IPromise<TSPerson> {
        let restPers = {};
        restPers = this.ebeguRestUtil.personToRestObject(restPers, person);

        return this.http.put(this.serviceURL, restPers, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
             this.log.debug('PARSING person REST object ', response.data);
                return this.ebeguRestUtil.parsePerson(new TSPerson(), response.data);
            }
        );
    }

    public create(person: TSPerson): IHttpPromise<TSPerson> {
        let pers = {};
        pers = this.ebeguRestUtil.personToRestObject(pers, person);
        return this.http.post(this.serviceURL, pers, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public findPerson(personID: string): IPromise<TSPerson> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(personID))
            .then((response: any) => {
                this.log.debug('PARSING person REST object ', response.data);
                return this.ebeguRestUtil.parsePerson(new TSPerson(), response.data);
            });

    }

    public getServiceName(): string {
        return 'PersonRS';
    }

}

