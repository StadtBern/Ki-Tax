/// <reference path="../../../../typings/browser.d.ts" />
/// <reference path="../../../models/TSStammdaten.ts" />
module app.StammdatenView {
    'use strict';
    import EnumEx = ebeguWeb.utils.EnumEx;
    import TSGeschlecht = ebeguWeb.API.TSGeschlecht;
    import DateUtil = ebeguWeb.utils.DateUtil;
    import TSAdressetyp = ebeguWeb.API.TSAdressetyp;

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
        showUmzug : boolean;
        showKorrespondadr : boolean;

        static $inject = [];
        /* @ngInject */
        constructor() {
            this.stammdaten = new ebeguWeb.API.TSStammdaten();
            let wohnAdr =  new ebeguWeb.API.TSAdresse();
            wohnAdr.adresseTyp = TSAdressetyp.WOHNADRESSE;
            this.stammdaten.adresse = wohnAdr;

            this.geschlechter = EnumEx.getNames(TSGeschlecht);
            this.showUmzug = false;
            this.showKorrespondadr = false;
        }

        submit () {
        }

        removeRow() {
        }

        umzugadreseClicked() {
            if(this.showUmzug) {
                let umzugAdr = new ebeguWeb.API.TSAdresse();
                umzugAdr.showDatumVon = true;
                umzugAdr.adresseTyp = TSAdressetyp.WOHNADRESSE;
                this.stammdaten.umzugadresse = umzugAdr;
            } else{
                this.stammdaten.umzugadresse = undefined;

            }
        }
        korrespondenzAdrClicked() {
            if(this.showKorrespondadr) {
                let korrAdr = new ebeguWeb.API.TSAdresse();
                korrAdr.showDatumVon = false;
                korrAdr.adresseTyp = TSAdressetyp.KORRESPONDENZADRESSE;
                this.stammdaten.korrespondenzAdresse = korrAdr;
            } else{
                this.stammdaten.korrespondenzAdresse = undefined;

            }
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
