import {IHttpService, ILogService, IPromise, IHttpPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {TSTraegerschaft} from '../../models/TSTraegerschaft';


export class TraegerschaftRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'traegerschaften';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public findTraegerschaft(traegerschaftID: string): IPromise<TSTraegerschaft> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(traegerschaftID))
            .then((response: any) => {
                this.log.debug('PARSING traegerschaft REST object ', response.data);
                return this.ebeguRestUtil.parseTraegerschaft(new TSTraegerschaft(), response.data);
            });
    }

    public createTraegerschaft(traegerschaft: TSTraegerschaft): IPromise<TSTraegerschaft> {
        return this.saveTraegerschaft(traegerschaft);
    }

    public updateTraegerschaft(traegerschaft: TSTraegerschaft): IPromise<TSTraegerschaft> {
        return this.saveTraegerschaft(traegerschaft);
    }

    private saveTraegerschaft(traegerschaft: TSTraegerschaft) {
        let restTraegerschaft = {};
        restTraegerschaft = this.ebeguRestUtil.traegerschaftToRestObject(restTraegerschaft, traegerschaft);
        return this.http.put(this.serviceURL, restTraegerschaft, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING traegerschaft REST object ', response.data);
            return this.ebeguRestUtil.parseTraegerschaft(new TSTraegerschaft(), response.data);
        });
    }

    public removeTraegerschaft(traegerschaftID: string): IHttpPromise<TSTraegerschaft> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(traegerschaftID));
    }

    public getAllTraegerschaften(): IPromise<TSTraegerschaft[]> {
        return this.http.get(this.serviceURL).then((response: any) => {
            this.log.debug('PARSING traegerschaften REST array object', response.data);
            return this.ebeguRestUtil.parseTraegerschaften(response.data);
        });
    }


    public getServiceName(): string {
        return 'TraegerschaftRS';
    }

}
