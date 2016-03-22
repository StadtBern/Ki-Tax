/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.services {
    'use strict';

    export interface IAdresseRS {
        serviceURL: string;
        http: angular.IHttpService;
    }

    export class AdresseRS implements IAdresseRS {
        serviceURL: string;
        http: angular.IHttpService;

        static $inject = ['$http', 'REST_API'];
        /* @ngInject */
        constructor($http: angular.IHttpService, REST_API: string) {
            this.serviceURL = REST_API + 'adressen';
            this.http = $http;
        }

        static instance ($http, REST_API) : IAdresseRS {
            return new AdresseRS($http, REST_API);
        }
    }

    angular.module('ebeguWeb.core').factory('adresseRS', AdresseRS.instance);

}
