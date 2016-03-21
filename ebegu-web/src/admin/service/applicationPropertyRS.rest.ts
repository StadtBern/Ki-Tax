/// <reference path="../../../typings/browser.d.ts" />
/// <reference path="../../utils/ebeguRestUtil.ts" />
module ebeguWeb.services {
    import EbeguRestUtil = ebeguWeb.utils.EbeguRestUtil;
    'use strict';

    export interface IApplicationPropertyRS {
        serviceURL: string;
        http: angular.IHttpService;

        getByName: (name: string) => angular.IPromise<any>;
        create: (name: string, value: string) => angular.IHttpPromise<any>;
        update: (name: string, value: string) => angular.IHttpPromise<any>;
        remove: (name: string) => angular.IHttpPromise<any>;
        getAllApplicationProperties: () => angular.IPromise<any>;
    }

    export class ApplicationPropertyRS implements IApplicationPropertyRS {
        serviceURL: string;
        http: angular.IHttpService;

        static $inject = ['$http', 'REST_API'];
        /* @ngInject */
        constructor($http: angular.IHttpService, REST_API: string) {
            this.serviceURL = REST_API + 'application-properties';
            this.http = $http;
        }

        getByName(name) {
            return this.http.get(this.serviceURL + '/' + encodeURIComponent(name)).then(
                (response: any) => EbeguRestUtil.parseApplicationProperties(response)
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
                (response: any) => EbeguRestUtil.parseApplicationProperties(response)
            );
        }

        static instance($http, REST_API): IApplicationPropertyRS {
            return new ApplicationPropertyRS($http, REST_API);
        }

    }


    angular.module('ebeguWeb.admin').service('applicationPropertyRS', ApplicationPropertyRS);

}
