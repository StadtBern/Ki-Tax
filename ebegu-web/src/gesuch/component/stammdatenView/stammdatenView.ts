/// <reference path="../../../../typings/browser.d.ts" />
/// <reference path="../../../models/TSStammdaten.ts" />
/// <reference path="../../component/abstractGesuchView.ts" />
module ebeguWeb.StammdatenView {
    'use strict';
    import EnumEx = ebeguWeb.utils.EnumEx;
    import EnumGeschlecht = ebeguWeb.API.EnumGeschlecht;
    import DateUtil = ebeguWeb.utils.DateUtil;
    import AbstractGesuchViewController = ebeguWeb.GesuchView.AbstractGesuchViewController;

    class StammdatenViewComponentConfig implements angular.IComponentOptions {
        transclude: boolean;
        bindings: any;
        templateUrl: string | Function;
        controller: any;
        controllerAs: string;

        constructor() {
            this.transclude = false;
            this.bindings = {};
            this.templateUrl = 'src/gesuch/component/stammdatenView/stammdatenView.html';
            this.controller = StammdatenViewController;
            this.controllerAs = 'vm';
        }
    }


    class StammdatenViewController extends AbstractGesuchViewController {
        stammdaten: ebeguWeb.API.TSStammdaten;
        geschlechter: Array<string>;

        static $inject = ['$state'];
        /* @ngInject */
        constructor($state: angular.ui.IStateService) {
            super($state);
            this.stammdaten = new ebeguWeb.API.TSStammdaten();
            this.stammdaten.adresse = new ebeguWeb.API.TSAdresse();
            let umzugAdr = new ebeguWeb.API.TSAdresse();
            umzugAdr.ort = 'Bern';
            umzugAdr.gueltigAb = undefined;
            this.stammdaten.umzugadresse = umzugAdr;
            this.geschlechter = EnumEx.getNames(EnumGeschlecht);
        }

        submit (form: angular.IFormController) {
            if (form.$valid) {
                //do all things
                //this.state.go("next.step"); //go to the next step
            }
        }

        removeRow() {
        }

        createItem() {
            // this.stammdaten = new ebeguWeb.API.TSStammdaten('', '', undefined, '', '', '', false);
        }

        resetForm() {
            this.stammdaten = undefined;
        }

        previousStep() {
            this.state.go("gesuch.familiensituation");
        }

    }

    angular.module('ebeguWeb.gesuch').component('stammdatenView', new StammdatenViewComponentConfig());

}
