/// <reference path="../../../../typings/browser.d.ts" />
module ebeguWeb.KinderView {
    'use strict';

    class KinderViewComponentConfig implements angular.IComponentOptions {
        transclude: boolean;
        bindings: any;
        templateUrl: string | Function;
        controller: any;
        controllerAs: string;

        constructor() {
            this.transclude = false;
            this.bindings = {};
            this.templateUrl = 'src/gesuch/component/kinderView/kinderView.html';
            this.controller = KinderViewController;
            this.controllerAs = 'vm';
        }
    }

    class KinderViewController  {

        static $inject = [];
        /* @ngInject */
        constructor() {

        }
    }

    angular.module('ebeguWeb.gesuch').component('kinderView', new KinderViewComponentConfig());

}
