/// <reference path="../../../../typings/browser.d.ts" />
/// <reference path="../../component/abstractGesuchView.ts" />
/// <reference path="../../../models/TSFamiliensituation.ts" />
/// <reference path="../../service/familiensituationRS.rest.ts" />
module ebeguWeb.FamiliensituationView {
    import AbstractGesuchViewController = ebeguWeb.GesuchView.AbstractGesuchViewController;
    import TSFamiliensituation = ebeguWeb.API.TSFamiliensituation;
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
        familiensituation: TSFamiliensituation;
        familienSituationRS: ebeguWeb.services.IFamiliensituationRS;

        static $inject = ['$state'];
        /* @ngInject */
        constructor($state: angular.ui.IStateService) {
            super($state);
            this.familiensituation = new TSFamiliensituation();
        }

        submit ($form: angular.IFormController) {
            if ($form.$valid) {
                //testen ob aktuelles familiensituation schon gespeichert ist
                if (this.familiensituation.timestampErstellt) {
                    this.familienSituationRS.update(this.familiensituation);
                } else {
                    this.familienSituationRS.create(this.familiensituation);
                }
                this.state.go("gesuch.stammdaten");
            }
        }

        showBeantragen(): boolean {
            return this.familiensituation.familiensituation === 'ALLEINERZIEHEND' || this.familiensituation.familiensituation === 'WENIGER_FUENF_JAHRE';
        }

    }

    angular.module('ebeguWeb.gesuch').component('familiensituationView', new FamiliensituationViewComponentConfig());

}
