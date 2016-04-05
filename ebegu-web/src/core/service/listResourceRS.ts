/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.services {
    'use strict';

    export interface IListResourceRS {
        serviceURL: string;
        http: angular.IHttpService;
        ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil;

        getLaenderList: () => angular.IPromise<any>;
    }

    export class ListResourceRS implements IListResourceRS {
        serviceURL: string;
        http: angular.IHttpService;
        ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil;
        static laenderList: Array<ebeguWeb.API.TSLand>;

        static $inject = ['$http', 'REST_API', 'ebeguRestUtil'];
        /* @ngInject */
        constructor($http: angular.IHttpService, REST_API: string,
                    ebeguRestUtil:ebeguWeb.utils.EbeguRestUtil) {
            this.serviceURL = REST_API + 'lists';
            this.http = $http;
            this.ebeguRestUtil = ebeguRestUtil;
            ListResourceRS.laenderList = [];
        }

        getLaenderList() {
            return this.http.get(this.serviceURL + '/laender', { cache: true }).then((response: any) => {
                if (ListResourceRS.laenderList.length <= 0) { // wenn die Laenderliste schon ausgefuellt wurde, nichts machen
                    for (var i = 0; i < response.data.length; i++) {
                        ListResourceRS.laenderList.push(this.ebeguRestUtil.landCodeToTSLand(response.data[i]));
                    }
                }
                return ListResourceRS.laenderList;
            });
        }

        static instance ($http, REST_API, ebeguRestUtil) : IListResourceRS {
            return new ListResourceRS($http, REST_API, ebeguRestUtil);
        }
    }

    angular.module('ebeguWeb.core').factory('listResourceRS', ListResourceRS.instance);

}
