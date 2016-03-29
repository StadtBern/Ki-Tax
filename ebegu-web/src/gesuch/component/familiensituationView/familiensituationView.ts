/// <reference path="../../../../typings/browser.d.ts" />
/// <reference path="../../component/abstractGesuchView.ts" />
module ebeguWeb.FamiliensituationView {
    import AbstractGesuchViewController = ebeguWeb.GesuchView.AbstractGesuchViewController;
    'use strict';

    class FamiliensituationViewComponentConfig implements angular.IComponentOptions {
        transclude: boolean;
        bindings: any;
        templateUrl: string | Function;
        controller: any;
        controllerAs: string;

        constructor() {
            this.transclude = false;
            this.bindings = {};
            this.templateUrl = 'src/gesuch/component/familiensituationView/familiensituationView.html';
            this.controller = FamiliensituationViewController;
            this.controllerAs = 'vm';
        }
    }


    class FamiliensituationViewController extends AbstractGesuchViewController {
        gesuch: any; // todo Add right type

        static $inject = ['$state'];
        /* @ngInject */
        constructor($state: angular.ui.IStateService) {
            super($state);
        }

        submit ($form: angular.IFormController) {
            if ($form.$valid) {
                this.state.go("gesuch.stammdaten");
            }
        }

    }

    angular.module('ebeguWeb.gesuch').component('familiensituationView', new FamiliensituationViewComponentConfig());

}
