/// <reference path="../../../../typings/browser.d.ts" />
/// <reference path="../../../models/TSPerson.ts" />
/// <reference path="../../component/abstractGesuchView.ts" />
module ebeguWeb.StammdatenView {
    'use strict';
    import EnumEx = ebeguWeb.utils.EnumEx;
    import GesuchForm = ebeguWeb.services.GesuchForm;
    import DateUtil = ebeguWeb.utils.DateUtil;
    import TSAdressetyp = ebeguWeb.API.TSAdressetyp;
    import TSAdresse = ebeguWeb.API.TSAdresse;
    import TSPerson = ebeguWeb.API.TSPerson;
    import EbeguRestUtil = ebeguWeb.utils.EbeguRestUtil;
    import AbstractGesuchViewController = ebeguWeb.GesuchView.AbstractGesuchViewController;
    import TSGeschlecht = ebeguWeb.API.TSGeschlecht;
    import IGesuchRS = ebeguWeb.services.IGesuchRS;


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
        gesuchRS: IGesuchRS;
        gesuchForm: GesuchForm;
        geschlechter:Array<string>;
        showUmzug:boolean;
        showKorrespondadr:boolean;
        personRS:ebeguWeb.services.IPersonRS;
        ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil;
        gesuchstellerNumber: number;

        static $inject = ['$stateParams', 'personRS', '$state','ebeguRestUtil', 'gesuchRS', 'gesuchForm'];
        /* @ngInject */
        constructor($stateParams: ebeguWeb.routes.IStammdatenStateParams, _personRS_, $state:angular.ui.IStateService,ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil,
                    gesuchRS: IGesuchRS, gesuchForm: GesuchForm) {
            super($state);
            this.gesuchForm = gesuchForm;
            this.gesuchRS = gesuchRS;
            this.personRS = _personRS_;
            this.ebeguRestUtil = ebeguRestUtil;
            this.setGesuchstellerNumber($stateParams.gesuchstellerNumber);
            this.initViewmodel();
        }

        private initViewmodel() {
            this.setStammdatenToWorkWith(new ebeguWeb.API.TSPerson());
            let wohnAdr = new ebeguWeb.API.TSAdresse();
            wohnAdr.adresseTyp = TSAdressetyp.WOHNADRESSE;
            this.getStammdatenToWorkWith().adresse = wohnAdr;
            this.getStammdatenToWorkWith().umzugAdresse = undefined;
            this.getStammdatenToWorkWith().korrespondenzAdresse = undefined;
            this.geschlechter = EnumEx.getNames(TSGeschlecht);
            this.showUmzug = false;
            this.showKorrespondadr = false;
        }

        private setGesuchstellerNumber(gsNumber: number) {
            //todo team ueberlegen ob es by default 1 sein muss oder ob man irgendeinen Fehler zeigen soll
            if (gsNumber == 1 || gsNumber == 2) {
                this.gesuchstellerNumber = gsNumber;
            }
            else {
                this.gesuchstellerNumber = 1;
            }
        }

        submit(form:angular.IFormController) {
            if (form.$valid) {
                //do all things
                //this.state.go("next.step"); //go to the next step
                if (!this.showUmzug) {
                    this.getStammdatenToWorkWith().umzugAdresse = undefined;
                }
                if (!this.showKorrespondadr) {
                    this.getStammdatenToWorkWith().korrespondenzAdresse = undefined;
                }

                this.gesuchRS.update(this.gesuchForm.gesuch).then((gesuchResponse: any) => {
                    this.gesuchForm.gesuch = gesuchResponse.data;
                    this.nextStep();
                });

            }
        }


        umzugadreseClicked() {
            if (this.showUmzug) {
                this.getStammdatenToWorkWith().umzugAdresse = this.initUmzugadresse();
            } else {
                this.getStammdatenToWorkWith().umzugAdresse = undefined;
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
                this.getStammdatenToWorkWith().korrespondenzAdresse = this.initKorrespondenzAdresse();
            } else {
                this.getStammdatenToWorkWith().korrespondenzAdresse = undefined;
            }
        }

        resetForm() {
            this.setStammdatenToWorkWith(undefined);
            this.initViewmodel();
        }

        previousStep() {
            this.state.go("gesuch.familiensituation");
        }

        nextStep() {
            if((this.gesuchstellerNumber == 1) && this.gesuchForm.isGesuchsteller2Required()) {
                this.state.go("gesuch.stammdaten", {gesuchstellerNumber:2});
            }
            else {
                this.state.go("gesuch.kinder");
            }
        }

        private getStammdatenToWorkWith():ebeguWeb.API.TSPerson {
            if(this.gesuchstellerNumber == 1) {
                return this.gesuchForm.gesuch.gesuchsteller1;
            }
            else {
                return this.gesuchForm.gesuch.gesuchsteller2;
            }
        }

        private setStammdatenToWorkWith(stammdaten: ebeguWeb.API.TSPerson):void {
            if(this.gesuchstellerNumber == 1) {
                this.gesuchForm.gesuch.gesuchsteller1 = stammdaten;
            }
            else {
                this.gesuchForm.gesuch.gesuchsteller2 = stammdaten;
            }
        }

    }

    angular.module('ebeguWeb.gesuch').component('stammdatenView', new StammdatenViewComponentConfig());

}
