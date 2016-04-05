/// <reference path="../../../../typings/browser.d.ts" />
/// <reference path="../../component/abstractGesuchView.ts" />
/// <reference path="../../../models/TSGesuch.ts" />
module ebeguWeb.FamiliensituationView {
    import AbstractGesuchViewController = ebeguWeb.GesuchView.AbstractGesuchViewController;
    import TSGesuch = ebeguWeb.API.TSGesuch;
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
        gesuch: TSGesuch;

        static $inject = ['$state'];
        /* @ngInject */
        constructor($state: angular.ui.IStateService) {
            super($state);
            this.gesuch = new TSGesuch();
        }

        submit ($form: angular.IFormController) {
            if ($form.$valid) {
                this.state.go("gesuch.stammdaten");
            }
        }

        showBeantragen(): boolean {
            return this.gesuch.familiensituation === 'ALLEINERZIEHEND' || this.gesuch.familiensituation === 'WENIGER_FUENF_JAHRE';
        }

    }

    angular.module('ebeguWeb.gesuch').component('familiensituationView', new FamiliensituationViewComponentConfig());

}
