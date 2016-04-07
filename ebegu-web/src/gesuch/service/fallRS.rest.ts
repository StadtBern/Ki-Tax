/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.services {
    'use strict';

    export interface IFallRS extends IEntityRS {
        findFall: (fallID:string) => angular.IHttpPromise<any>;
        create: (fall:ebeguWeb.API.TSFall) => angular.IHttpPromise<any>;
        update: (fall:ebeguWeb.API.TSFall) => angular.IHttpPromise<any>;
    }

    export class FallRS implements IFallRS {
        serviceURL:string;
        http:angular.IHttpService;
        ebeguRestUtil:ebeguWeb.utils.EbeguRestUtil;

        static $inject = ['$http', 'REST_API', 'ebeguRestUtil'];
        /* @ngInject */
        constructor($http:angular.IHttpService, REST_API:string, ebeguRestUtil:ebeguWeb.utils.EbeguRestUtil) {
            this.serviceURL = REST_API + 'falle';
            this.http = $http;
            this.ebeguRestUtil = ebeguRestUtil;
        }

        public create(fall) {
            let returnedFall = {};
            returnedFall = this.ebeguRestUtil.fallToRestObject(returnedFall,fall);
            return this.http.post(this.serviceURL, returnedFall, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }

        public update(fall) {
            let returnedFall = {};
            returnedFall = this.ebeguRestUtil.fallToRestObject(returnedFall,fall);
            return this.http.put(this.serviceURL,  returnedFall, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }

        public findFall(fallID:string) {
            return this.http.get( this.serviceURL + '/' + encodeURIComponent(fallID));
        }

        static instance ($http, REST_API, ebeguRestUtil) : IFallRS {
            return new FallRS($http, REST_API, ebeguRestUtil);
        }

    }

    angular.module('ebeguWeb.core').factory('fallRS', FallRS.instance);

}
