/// <reference path="../../../../typings/browser.d.ts" />
/// <reference path="../../component/abstractGesuchView.ts" />
/// <reference path="../../../models/TSFamiliensituation.ts" />
/// <reference path="../../service/familiensituationRS.rest.ts" />
module ebeguWeb.FamiliensituationView {
    import EnumEx = ebeguWeb.utils.EnumEx;
    import AbstractGesuchViewController = ebeguWeb.GesuchView.AbstractGesuchViewController;
    import TSFamiliensituation = ebeguWeb.API.TSFamiliensituation;
    import TSFamilienstatus = ebeguWeb.API.TSFamilienstatus;
    import TSGesuchKardinalitaet = ebeguWeb.API.TSGesuchKardinalitaet;
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
        familiensituationRS: ebeguWeb.services.IFamiliensituationRS;
        familienstatusValues: Array<TSFamilienstatus>;
        gesuchKardinalitaetValues: Array<TSGesuchKardinalitaet>;

        static $inject = ['$state', 'familiensituationRS'];
        /* @ngInject */
        constructor($state: angular.ui.IStateService, familiensituationRS: ebeguWeb.services.IFamiliensituationRS) {
            super($state);
            this.familiensituation = new TSFamiliensituation();
            this.familiensituationRS = familiensituationRS;
            this.familienstatusValues = ebeguWeb.API.getTSFamilienstatusValues();
            this.gesuchKardinalitaetValues = ebeguWeb.API.getTSGesuchKardinalitaetValues();
        }

        submit ($form: angular.IFormController) {
            if ($form.$valid) {
                //testen ob aktuelles familiensituation schon gespeichert ist
                if (this.familiensituation.timestampErstellt) {
                    this.familiensituationRS.update(this.familiensituation);
                } else {
                    this.familiensituationRS.create(this.familiensituation);
                }
                this.state.go("gesuch.stammdaten");
            }
        }

        showGesuchKardinalitaet(): boolean {
            return this.familiensituation.familienstatus === TSFamilienstatus.ALLEINERZIEHEND
                || this.familiensituation.familienstatus === TSFamilienstatus.WENIGER_FUENF_JAHRE;
        }

    }

    angular.module('ebeguWeb.gesuch').component('familiensituationView', new FamiliensituationViewComponentConfig());

}
