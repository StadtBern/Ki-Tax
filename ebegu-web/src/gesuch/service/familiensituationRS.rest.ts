/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.services {
    import IEntityRS = ebeguWeb.services.IEntityRS;
    'use strict';

    export interface IFamiliensituationRS extends IEntityRS {
        findFamiliensituation: (familiensituationID: string) => angular.IHttpPromise<any>;
        create: (familiensituation: ebeguWeb.API.TSFamiliensituation) => angular.IHttpPromise<any>;
        update: (familiensituation: ebeguWeb.API.TSFamiliensituation) => angular.IHttpPromise<any>;
    }

    export class FamiliensituationRS implements IFamiliensituationRS {
        serviceURL:string;
        http:angular.IHttpService;
        ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil;

        static $inject = ['$http', 'REST_API', 'ebeguRestUtil'];
        /* @ngInject */
        constructor($http:angular.IHttpService, REST_API:string, ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil) {
            this.serviceURL = REST_API + 'familiensituation';
            this.http = $http;
            this.ebeguRestUtil = ebeguRestUtil;
        }

        public create(familiensituation) {
            let returnedFamiliensituation = {};
            returnedFamiliensituation = this.ebeguRestUtil.familiensituationToRestObject(returnedFamiliensituation,familiensituation);
            return this.http.post(this.serviceURL, returnedFamiliensituation, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }

        public update(familiensituation) {
            let returnedFamiliensituation = {};
            returnedFamiliensituation = this.ebeguRestUtil.familiensituationToRestObject(returnedFamiliensituation,familiensituation);
            return this.http.put(this.serviceURL,  returnedFamiliensituation, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }

        public findFamiliensituation(familiensituationID:string) {
            return this.http.get( this.serviceURL + '/' + encodeURIComponent(familiensituationID));
        }

        static instance ($http, REST_API, ebeguRestUtil) : IFamiliensituationRS {
            return new FamiliensituationRS($http, REST_API, ebeguRestUtil);
        }

    }

    angular.module('ebeguWeb.core').factory('familiensituationRS', FamiliensituationRS.instance);

}
