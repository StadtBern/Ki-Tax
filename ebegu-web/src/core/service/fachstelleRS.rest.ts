import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IHttpPromise, IPromise} from 'angular';
import {TSFachstelle} from '../../models/TSFachstelle';

export class FachstelleRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil) {
        this.serviceURL = REST_API + 'fachstellen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public update(fachstelle: TSFachstelle): IHttpPromise<any> {
        let fachstelleObject = {};
        fachstelleObject = this.ebeguRestUtil.fachstelleToRestObject(fachstelleObject, fachstelle);

        return this.http.put(this.serviceURL, fachstelleObject, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public create(fachstelle: TSFachstelle): IHttpPromise<any> {
        let fachstelleObject = {};
        fachstelleObject = this.ebeguRestUtil.fachstelleToRestObject(fachstelleObject, fachstelle);

        return this.http.post(this.serviceURL, fachstelleObject, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public remove(fachstelleID: string): IHttpPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(fachstelleID));
    }

    public findFachstelle(fachstelleID: string): IHttpPromise<any> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(fachstelleID));
    }

    public getAllFachstellen(): IPromise<TSFachstelle[]> {
        return this.http.get(this.serviceURL + '/').then(
            (response: any) => this.ebeguRestUtil.parseFachstellen(response.data)
        );
    }

}
