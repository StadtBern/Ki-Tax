/// <reference path="../../../../typings/browser.d.ts" />
/// <reference path="../../../models/TSStammdaten.ts" />
module app.StammdatenView {
    'use strict';
    import EnumEx = ebeguWeb.utils.EnumEx;
    import TSGeschlecht = ebeguWeb.API.TSGeschlecht;
    import DateUtil = ebeguWeb.utils.DateUtil;

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
        geschlechter: Array<string>;

        static $inject = [];
        /* @ngInject */
        constructor() {
            this.stammdaten = new ebeguWeb.API.TSStammdaten();
            this.stammdaten.adresse = new ebeguWeb.API.TSAdresse();
            let umzugAdr = new ebeguWeb.API.TSAdresse();
            umzugAdr.ort = 'Bern';
            umzugAdr.gueltigAb = undefined;
            this.stammdaten.umzugadresse = umzugAdr;
            this.geschlechter = EnumEx.getNames(TSGeschlecht);
        }

        submit () {
            console.log(this.stammdaten)
        }

        removeRow() {
        }

        createItem() {
            // this.stammdaten = new ebeguWeb.API.TSStammdaten('', '', undefined, '', '', '', false);
        }

        resetForm() {
            this.stammdaten = undefined;
        }



    }

    angular.module('ebeguWeb.stammdaten').component('stammdatenView', new StammdatenViewComponentConfig());

}
