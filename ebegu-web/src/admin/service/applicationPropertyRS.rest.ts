import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSApplicationProperty from '../../models/TSApplicationProperty';
import {IHttpService, IPromise, IHttpPromise} from 'angular';

export default class ApplicationPropertyRS {
    static $inject = ['$http', 'REST_API', 'EbeguRestUtil'];

    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil) {
        this.serviceURL = REST_API + 'application-properties';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    static instance($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil): ApplicationPropertyRS {
        return new ApplicationPropertyRS($http, REST_API, ebeguRestUtil);
    }

    getByName(name: string): IPromise<TSApplicationProperty> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(name)).then(
            (response: any) => this.ebeguRestUtil.parseApplicationProperties(response.data)[0]
        );
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

    public remove(name: string): IHttpPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(name));
    }

    getAllApplicationProperties(): IPromise<TSApplicationProperty[]> {
        return this.http.get(this.serviceURL + '/').then(
            (response: any) => this.ebeguRestUtil.parseApplicationProperties(response.data)
        );
    }
}
