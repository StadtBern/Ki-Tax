/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.services {
    import EbeguRestUtil = ebeguWeb.utils.EbeguRestUtil;
    'use strict';

    export interface IPersonRS {
        serviceURL:string;
        http:angular.IHttpService;

        create:(person:ebeguWeb.API.TSPerson)  => angular.IHttpPromise<any>;
        update:(person:ebeguWeb.API.TSPerson)  => angular.IHttpPromise<any>;
        findPerson:(personID:string)  => angular.IHttpPromise<any>;
    }

    export class PersonRS implements IPersonRS {
        serviceURL:string;
        http:angular.IHttpService;

        static $inject = ['$http', 'REST_API'];
        /* @ngInject */
        constructor($http:angular.IHttpService, REST_API:string) {
            this.serviceURL = REST_API + 'personen';
            this.http = $http;
        }

        public update(person) {
            let pers = {};
             pers = EbeguRestUtil.personToRestObject(pers,person)

            return this.http.put(this.serviceURL,  pers, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }

        public create(person) {
            let pers = {};
            pers = EbeguRestUtil.personToRestObject(pers,person)
            return this.http.post(this.serviceURL, pers, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }

        public findPerson(personID) {
            return this.http.get( this.serviceURL + '/' + encodeURIComponent(personID));
        }


        static instance($http, REST_API):IPersonRS {
            return new PersonRS($http, REST_API);
        }
    }

    angular.module('ebeguWeb.core').factory('personRS', PersonRS.instance);

}
