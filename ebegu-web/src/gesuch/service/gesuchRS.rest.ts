/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.services {
    'use strict';

    export interface IGesuchRS {
        serviceURL: string;
        http: angular.IHttpService;
        ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil;

        findGesuch: (gesuchID:string) => angular.IHttpPromise<any>;
        create: (gesuch:ebeguWeb.API.TSGesuch) => angular.IHttpPromise<any>;
        update: (gesuch:ebeguWeb.API.TSGesuch) => angular.IHttpPromise<any>;
    }

    export class GesuchRS implements IGesuchRS {
        serviceURL:string;
        http:angular.IHttpService;
        ebeguRestUtil:ebeguWeb.utils.EbeguRestUtil;

        static $inject = ['$http', 'REST_API', 'ebeguRestUtil'];
        /* @ngInject */
        constructor($http:angular.IHttpService, REST_API:string, ebeguRestUtil:ebeguWeb.utils.EbeguRestUtil) {
            this.serviceURL = REST_API + 'gesuche';
            this.http = $http;
            this.ebeguRestUtil = ebeguRestUtil;
        }

        public create(gesuch) {
            let returnedGesuch = {};
            returnedGesuch = this.ebeguRestUtil.gesuchToRestObject(returnedGesuch,gesuch);
            return this.http.post(this.serviceURL, returnedGesuch, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }

        public update(gesuch) {
            let returnedGesuch = {};
            returnedGesuch = this.ebeguRestUtil.gesuchToRestObject(returnedGesuch,gesuch);
            return this.http.put(this.serviceURL,  returnedGesuch, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }

        public findGesuch(gesuchID:string) {
            return this.http.get( this.serviceURL + '/' + encodeURIComponent(gesuchID));
        }

        static instance ($http, REST_API, ebeguRestUtil) : IGesuchRS {
            return new GesuchRS($http, REST_API, ebeguRestUtil);
        }

    }

    angular.module('ebeguWeb.core').factory('gesuchRS', GesuchRS.instance);

}

