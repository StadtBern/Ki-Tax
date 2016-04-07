/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.services {
    'use strict';

    export interface IAdresseRS extends IEntityRS {

        create: (adresse: ebeguWeb.API.TSAdresse)  => angular.IHttpPromise<any>;
    }

    export class AdresseRS implements IAdresseRS {
        ebeguRestUtil:ebeguWeb.utils.EbeguRestUtil;
        serviceURL: string;
        http: angular.IHttpService;

        static $inject = ['$http', 'REST_API'];
        /* @ngInject */
        constructor($http: angular.IHttpService, REST_API: string) {
            this.serviceURL = REST_API + 'adressen';
            this.http = $http;
        }

        public create(adresse) {
            return this.http.post(this.serviceURL, adresse, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }

        static instance ($http, REST_API) : IAdresseRS {
            return new AdresseRS($http, REST_API);
        }
    }

    angular.module('ebeguWeb.core').factory('adresseRS', AdresseRS.instance);

}
