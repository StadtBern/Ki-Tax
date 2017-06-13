import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IHttpPromise} from 'angular';
import IPromise = angular.IPromise;


export class DatabaseMigrationRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil) {
        this.serviceURL = REST_API + 'dbmigration';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public getServiceName(): string {
        return 'DatabaseMigrationRS';
    }

    public processScript(scriptNr: string): IHttpPromise<any> {
        return this.http.get(this.serviceURL + '/' + scriptNr);
    }
}
