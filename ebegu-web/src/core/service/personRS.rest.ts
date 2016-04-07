/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.services {
    import EbeguRestUtil = ebeguWeb.utils.EbeguRestUtil;
    'use strict';

    export interface IPersonRS extends IEntityRS {
        create:(person:ebeguWeb.API.TSPerson)  => angular.IHttpPromise<any>;
        update:(person:ebeguWeb.API.TSPerson)  => angular.IHttpPromise<any>;
        findPerson:(personID:string)  => angular.IHttpPromise<any>;
    }

    export class PersonRS implements IPersonRS {
        serviceURL:string;
        http:angular.IHttpService;
        ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil;

        static $inject = ['$http', 'REST_API'];
        /* @ngInject */
        constructor($http:angular.IHttpService, REST_API:string, ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil) {
            this.serviceURL = REST_API + 'personen';
            this.http = $http;
            this.ebeguRestUtil = ebeguRestUtil;
        }

        public update(person) {
            let pers = {};
             pers = this.ebeguRestUtil.personToRestObject(pers,person);

            return this.http.put(this.serviceURL,  pers, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }

        public create(person) {
            let pers = {};
            pers = this.ebeguRestUtil.personToRestObject(pers,person);
            return this.http.post(this.serviceURL, pers, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }

        public findPerson(personID) {
            return this.http.get( this.serviceURL + '/' + encodeURIComponent(personID));
        }


        static instance($http, REST_API, ebeguRestUtil):IPersonRS {
            return new PersonRS($http, REST_API, ebeguRestUtil);
        }
    }

    angular.module('ebeguWeb.core').factory('personRS', PersonRS.instance);

}
