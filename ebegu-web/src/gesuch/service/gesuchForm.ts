/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.services {
    import TSFall = ebeguWeb.API.TSFall;
    import TSGesuch = ebeguWeb.API.TSGesuch;
    'use strict';

    export interface IGesuchForm {
        fall: ebeguWeb.API.TSFall;
        gesuch: ebeguWeb.API.TSGesuch;
    }

    export class GesuchForm implements IGesuchForm {
        fall:ebeguWeb.API.TSFall;
        gesuch:ebeguWeb.API.TSGesuch;

        static $inject = [];
        /* @ngInject */
        constructor() {
            this.fall = new TSFall();
            this.gesuch = new TSGesuch();
        }

        static instance () : IGesuchForm {
            return new GesuchForm();
        }
    }

    angular.module('ebeguWeb.core').factory('gesuchForm', GesuchForm.instance);

}
