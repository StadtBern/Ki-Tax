/// <reference path="../../../../typings/browser.d.ts" />
/// <reference path="../../../models/TSStammdaten.ts" />
module app.StammdatenView {
    'use strict';

    class StammdatenViewComponentConfig implements angular.IComponentOptions {
        transclude: boolean;
        bindings: any;
        templateUrl: string | Function;
        controller: any;
        controllerAs: string;

        constructor() {
            this.transclude = false;
            this.bindings = {};
            this.templateUrl = 'src/stammdaten/component/stammdatenView/stammdatenView.html';
            this.controller = StammdatenViewController;
            this.controllerAs = 'vm';
        }
    }


    class StammdatenViewController  {
        stammdaten: ebeguWeb.API.TSStammdaten;

        static $inject = [];
        /* @ngInject */
        constructor() {
            this.stammdaten = null;
        }

        submit () {
        }

        removeRow() {
        }

        createItem() {
            this.stammdaten = new ebeguWeb.API.TSStammdaten('', '', null, '', '', '', false);
        }

        resetForm() {
            this.stammdaten = null;
        }

    }

    angular.module('ebeguWeb.stammdaten').component('stammdatenView', new StammdatenViewComponentConfig());

}
