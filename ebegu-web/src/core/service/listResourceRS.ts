/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.services {
    'use strict';

    export interface IListResourceRS {
        serviceURL: string;
        http: angular.IHttpService;

        getLaenderList: () => angular.IHttpPromise<any>;
    }

    export class ListResourceRS implements IListResourceRS {
        serviceURL: string;
        http: angular.IHttpService;

        static $inject = ['$http', 'REST_API'];
        /* @ngInject */
        constructor($http: angular.IHttpService, REST_API: string) {
            this.serviceURL = REST_API + 'lists';
            this.http = $http;
        }

        getLaenderList() {
            return this.http.get(this.serviceURL + '/laender', { cache: true });
        }

        static instance ($http, REST_API) : IListResourceRS {
            return new ListResourceRS($http, REST_API);
        }
    }

    angular.module('ebeguWeb.core').factory('listResourceRS', ListResourceRS.instance);

}
