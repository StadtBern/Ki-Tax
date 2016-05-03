import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IHttpPromise, IPromise, ILogService} from 'angular';
import {TSFachstelle} from '../../models/TSFachstelle';

export class FachstelleRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'fachstellen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public updateFachstelle(fachstelle: TSFachstelle): IPromise<TSFachstelle> {
        return this.saveFachstelle(fachstelle);
    }

    public createFachstelle(fachstelle: TSFachstelle): IPromise<TSFachstelle> {
        return this.saveFachstelle(fachstelle);
    }

    private saveFachstelle(fachstelle: TSFachstelle): IPromise<TSFachstelle> {
        let fachstelleObject = {};
        fachstelleObject = this.ebeguRestUtil.fachstelleToRestObject(fachstelleObject, fachstelle);

        return this.http.put(this.serviceURL, fachstelleObject, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING fachstelle REST object ', response.data);
            return this.ebeguRestUtil.parseFachstelle(new TSFachstelle(), response.data);
        });
    }

    public removeFachstelle(fachstelleID: string): IHttpPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(fachstelleID));
    }

    public findFachstelle(fachstelleID: string): IPromise<TSFachstelle> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(fachstelleID))
            .then((response: any) => {
                this.log.debug('PARSING fachstelle REST object ', response.data);
                return this.ebeguRestUtil.parseFachstelle(new TSFachstelle(), response.data);
            });
    }

    public getAllFachstellen(): IPromise<TSFachstelle[]> {
        return this.http.get(this.serviceURL).then(
            (response: any) => this.ebeguRestUtil.parseFachstellen(response.data)
        );
    }

    public getServiceName(): string {
        return 'FachstelleRS';
    }

}
