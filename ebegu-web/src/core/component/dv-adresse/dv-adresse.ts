/// <reference path="../../../../typings/browser.d.ts" />
module app.DvAdresse {
    'use strict';

    export class ComponentConfig implements angular.IComponentOptions {
        transclude: boolean;
        bindings: any;
        templateUrl: string | Function;
        controller: any;
        controllerAs: string;

        constructor() {
            this.transclude = false;
            this.bindings = {};
            this.templateUrl = 'src/core/component/dv-adresse/dv-adresse.html';
            this.controller = DvAdresse;
            this.controllerAs = 'vm';
        }
    }


    export class DvAdresse  {
        adresse:ebeguWeb.API.TSAdresse;
        adresseRS:ebeguWeb.services.IAdresseRS;
        adressen:Array<ebeguWeb.API.TSAdresse>;

        static $inject = ['adressRS'];
        /* @ngInject */
        constructor(adresseRS:ebeguWeb.services.IAdresseRS) {
            this.adresse = null;
            this.adressen = null;
            this.adresseRS = adresseRS;
        }
    }

    angular.module('ebeguWeb.core').component('dvAdresse', new ComponentConfig());

}
