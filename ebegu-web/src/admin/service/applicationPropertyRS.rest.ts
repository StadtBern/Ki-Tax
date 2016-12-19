import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSApplicationProperty from '../../models/TSApplicationProperty';
import {IHttpService, IPromise, IHttpPromise} from 'angular';


export class ApplicationPropertyRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil) {
        this.serviceURL = REST_API + 'application-properties';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    getByName(name: string): IPromise<TSApplicationProperty> {
        return this.http.get(this.serviceURL + '/key/' + encodeURIComponent(name)).then(
            (response: any) => this.ebeguRestUtil.parseApplicationProperty(new TSApplicationProperty(), response.data)
        );
    }

    isDevMode(): IPromise<boolean> {
        return this.http.get(this.serviceURL + '/devmode').then((response) => {
            return response.data;
        });
    }

    create(name: string, value: string): IHttpPromise<any> {
        return this.http.post(this.serviceURL + '/' + encodeURIComponent(name), value, {
            headers: {
                'Content-Type': 'text/plain'
            }
        });
    }

    update(name: string, value: string): IHttpPromise<any> {
        return this.http.post(this.serviceURL + '/' + encodeURIComponent(name), value, {
            headers: {
                'Content-Type': 'text/plain'
            }
        });
    }

    remove(name: string): IHttpPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(name));
    }

    getAllApplicationProperties(): IPromise<TSApplicationProperty[]> {
        return this.http.get(this.serviceURL + '/').then(
            (response: any) => this.ebeguRestUtil.parseApplicationProperties(response.data)
        );
    }

}

