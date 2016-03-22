/// <reference path="../../../../typings/browser.d.ts" />
module app.StammdatenView {
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
            this.templateUrl = 'src/stammdaten/component/stammdatenView/stammdatenView.html';
            this.controller = StammdatenView;
            this.controllerAs = 'vm';
        }
    }


    class StammdatenView  {

        static $inject = [];
        /* @ngInject */
        constructor() {
            var vm = this;
        }
    }

    angular.module('ebeguWeb.stammdaten').component('stammdatenView', new ComponentConfig());

}
