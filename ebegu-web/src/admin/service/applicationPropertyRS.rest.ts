/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.services {
    'use strict';

    export interface IApplicationPropertyRS {
        serviceURL: string;
        http: angular.IHttpService;

        getByKey: (key: string) => any; // todo imanol add type
        create: (key: string, value: string) => any; // todo imanol add type
        remove: (key: string) => any; // todo imanol add type
        getAllApplicationProperties: () => any; // todo imanol add type
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

        getByKey(key) {
            return this.http.get(this.serviceURL + '/' + encodeURIComponent(key));
        }

        create(key, value) {
            return this.http.post(this.serviceURL + '/' + encodeURIComponent(key), value, {
                headers: {
                    'Content-Type': 'text/plain'
                }
            });
        }

        remove(key) {
            return this.http.delete(this.serviceURL + '/' + encodeURIComponent(key));
        }

        getAllApplicationProperties() {
            return this.http.get(this.serviceURL + '/');
        }

        static instance($http, REST_API): IApplicationPropertyRS {
            return new ApplicationPropertyRS($http, REST_API);
        }

    }


    angular.module('ebeguWeb.admin').factory('applicationPropertyRS', ApplicationPropertyRS.instance);

}
