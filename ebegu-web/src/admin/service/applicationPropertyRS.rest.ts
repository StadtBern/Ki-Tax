/// <reference path="../../../typings/browser.d.ts" />
/// <reference path="../../utils/EbeguRestUtil.ts" />
module ebeguWeb.services {
    import EbeguRestUtil = ebeguWeb.utils.EbeguRestUtil;
    'use strict';

    export interface IApplicationPropertyRS {
        serviceURL: string;
        http: angular.IHttpService;
        ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil;

        getByName: (name: string) => angular.IPromise<any>;
        create: (name: string, value: string) => angular.IHttpPromise<any>;
        update: (name: string, value: string) => angular.IHttpPromise<any>;
        remove: (name: string) => angular.IHttpPromise<any>;
        getAllApplicationProperties: () => angular.IPromise<any>;
    }

    export class ApplicationPropertyRS implements IApplicationPropertyRS {
        serviceURL: string;
        http: angular.IHttpService;
        ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil

        static $inject = ['$http', 'REST_API', 'ebeguRestUtil'];
        /* @ngInject */
        constructor($http: angular.IHttpService, REST_API: string, ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil) {
            this.serviceURL = REST_API + 'application-properties';
            this.http = $http;
            this.ebeguRestUtil = ebeguRestUtil;
        }

        getByName(name) {
            return this.http.get(this.serviceURL + '/' + encodeURIComponent(name)).then(
                (response: any) => this.ebeguRestUtil.parseApplicationProperties(response.data)
            );
        }

        create(name, value) {
            return this.http.post(this.serviceURL + '/' + encodeURIComponent(name), value, {
                headers: {
                    'Content-Type': 'text/plain'
                }
            });
        }

        update(name, value) {
            return this.http.post(this.serviceURL + '/' + encodeURIComponent(name), value, {
                headers: {
                    'Content-Type': 'text/plain'
                }
            });
        }

        remove(name) {
            return this.http.delete(this.serviceURL + '/' + encodeURIComponent(name));
        }

        getAllApplicationProperties() {
            return this.http.get(this.serviceURL + '/').then(
                (response: any) => this.ebeguRestUtil.parseApplicationProperties(response.data)
            );
        }

        static instance($http, REST_API, ebeguRestUtil): IApplicationPropertyRS {
            return new ApplicationPropertyRS($http, REST_API, ebeguRestUtil);
        }

    }


    angular.module('ebeguWeb.admin').service('applicationPropertyRS', ApplicationPropertyRS);

}
