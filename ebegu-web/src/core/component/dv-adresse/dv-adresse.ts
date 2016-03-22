/// <reference path="../../../../typings/browser.d.ts" />
module app.DvAdresse {
    'use strict';

    class ComponentConfig implements angular.IComponentOptions {
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


    class DvAdresse  {

        static $inject = [];
        /* @ngInject */
        constructor() {
            var vm = this;
        }
    }

    angular.module('ebeguWeb.core').component('dvAdresse', new ComponentConfig());

}
