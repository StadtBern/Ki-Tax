/// <reference path="../../../../typings/browser.d.ts" />
/// <reference path="../../../models/TSPerson.ts" />
/// <reference path="../../component/abstractGesuchView.ts" />
module app.StammdatenView {
    'use strict';
    import EnumEx = ebeguWeb.utils.EnumEx;
    // import TSGeschlecht = ebeguWeb.API.TSGeschlecht;
    import DateUtil = ebeguWeb.utils.DateUtil;
    import TSAdressetyp = ebeguWeb.API.TSAdressetyp;
    import TSAdresse = ebeguWeb.API.TSAdresse;
    import TSPerson = ebeguWeb.API.TSPerson;
    import EbeguRestUtil = ebeguWeb.utils.EbeguRestUtil;
    import AbstractGesuchViewController = ebeguWeb.GesuchView.AbstractGesuchViewController;
    import TSGeschlecht = ebeguWeb.API.TSGeschlecht;

    class StammdatenViewComponentConfig implements angular.IComponentOptions {
        transclude:boolean;
        bindings:any;
        templateUrl:string | Function;
        controller:any;
        controllerAs:string;

        constructor() {
            this.transclude = false;
            this.bindings = {};
            this.templateUrl = 'src/gesuch/component/stammdatenView/stammdatenView.html';
            this.controller = StammdatenViewController;
            this.controllerAs = 'vm';
        }
    }


    class StammdatenViewController extends AbstractGesuchViewController {
        stammdaten:ebeguWeb.API.TSPerson;
        geschlechter:Array<string>;
        showUmzug:boolean;
        showKorrespondadr:boolean;
        personRS:ebeguWeb.services.IPersonRS;
        ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil

        static $inject = ['personRS', '$state','ebeguRestUtil'];
        /* @ngInject */
        constructor(_personRS_, $state:angular.ui.IStateService,ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil) {
            super($state);
            this.initViewmodel();
            this.personRS = _personRS_;
            this.ebeguRestUtil = ebeguRestUtil;
        }

        private initViewmodel() {
            this.stammdaten = new ebeguWeb.API.TSPerson();
            let wohnAdr = new ebeguWeb.API.TSAdresse();
            wohnAdr.adresseTyp = TSAdressetyp.WOHNADRESSE;
            this.stammdaten.adresse = wohnAdr;
            this.stammdaten.umzugAdresse = undefined;
            this.stammdaten.korrespondenzAdresse = undefined;
            this.geschlechter = EnumEx.getNames(TSGeschlecht);
            this.showUmzug = false;
            this.showKorrespondadr = false;
        }

        submit(form:angular.IFormController) {
            if (form.$valid) {
                //do all things
                //this.state.go("next.step"); //go to the next step
                if (!this.showUmzug) {
                    this.stammdaten.umzugAdresse = undefined;
                }
                if (!this.showKorrespondadr) {
                    this.stammdaten.korrespondenzAdresse = undefined;
                }
                if (!this.stammdaten.timestampErstellt) {
                    //es handel sich um eine neue Person
                    this.personRS.create(this.stammdaten).then((response) => {
                            this.stammdaten = ebeguRestUtil.parsePerson(new TSPerson(), response.data);
                        }
                    );

                } else {
                    //update
                    this.personRS.update(this.stammdaten).then((response) => {
                            this.stammdaten = ebeguRestUtil.parsePerson(new TSPerson(), response.data);
                        }
                    );
                }
            }
        }


        umzugadreseClicked() {
            if (this.showUmzug) {
                this.stammdaten.umzugAdresse = this.initUmzugadresse();
            } else {
                this.stammdaten.umzugAdresse = undefined;
            }
        }

        private initUmzugadresse() {
            let umzugAdr = new ebeguWeb.API.TSAdresse();
            umzugAdr.showDatumVon = true;
            umzugAdr.adresseTyp = TSAdressetyp.WOHNADRESSE;
            return umzugAdr;
        }

        private  initKorrespondenzAdresse():TSAdresse {
            let korrAdr = new ebeguWeb.API.TSAdresse();
            korrAdr.showDatumVon = false;
            korrAdr.adresseTyp = TSAdressetyp.KORRESPONDENZADRESSE;
            return korrAdr;
        }

        korrespondenzAdrClicked() {
            if (this.showKorrespondadr) {
                var korrAdr = this.initKorrespondenzAdresse();
                this.stammdaten.korrespondenzAdresse = korrAdr;
            } else {
                this.stammdaten.korrespondenzAdresse = undefined;
            }
        }

        resetForm() {
            this.stammdaten = undefined;
            this.initViewmodel();
        }

        previousStep() {
            this.state.go("gesuch.familiensituation");
        }

    }

    angular.module('ebeguWeb.gesuch').component('stammdatenView', new StammdatenViewComponentConfig());

}
