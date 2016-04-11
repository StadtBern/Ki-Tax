/// <reference path="../../../../typings/browser.d.ts" />
/// <reference path="../../component/abstractGesuchView.ts" />
/// <reference path="../../../models/TSFamiliensituation.ts" />
/// <reference path="../../service/familiensituationRS.rest.ts" />
module ebeguWeb.FamiliensituationView {
    import EnumEx = ebeguWeb.utils.EnumEx;
    import AbstractGesuchViewController = ebeguWeb.GesuchView.AbstractGesuchViewController;
    import GesuchForm = ebeguWeb.services.GesuchForm;

    import TSFamiliensituation = ebeguWeb.API.TSFamiliensituation;

    import IFallRS = ebeguWeb.services.IFallRS;
    import IGesuchRS = ebeguWeb.services.IGesuchRS;
    import IFamiliensituationRS = ebeguWeb.services.IFamiliensituationRS;

    import TSFamilienstatus = ebeguWeb.API.TSFamilienstatus;
    import TSGesuchstellerKardinalitaet = ebeguWeb.API.TSGesuchstellerKardinalitaet;
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
        gesuchForm: GesuchForm;
        familiensituation: TSFamiliensituation;
        fallRS: IFallRS;
        gesuchRS: IGesuchRS;
        familiensituationRS: IFamiliensituationRS;
        familienstatusValues: Array<TSFamilienstatus>;
        gesuchstellerKardinalitaetValues: Array<TSGesuchstellerKardinalitaet>;

        static $inject = ['$state', 'familiensituationRS', 'fallRS', 'gesuchRS', 'gesuchForm'];
        /* @ngInject */
        constructor($state: angular.ui.IStateService, familiensituationRS: IFamiliensituationRS,
                    fallRS: IFallRS, gesuchRS: IGesuchRS, gesuchForm: GesuchForm) {
            super($state);
            this.gesuchForm = gesuchForm;
            this.familiensituation = new TSFamiliensituation();
            this.fallRS = fallRS;
            this.gesuchRS = gesuchRS;
            this.familiensituationRS = familiensituationRS;
            this.familienstatusValues = ebeguWeb.API.getTSFamilienstatusValues();
            this.gesuchstellerKardinalitaetValues = ebeguWeb.API.getTSGesuchstellerKardinalitaetValues();
        }

        submit ($form: angular.IFormController) {
            if ($form.$valid) {
                //testen ob aktuelles familiensituation schon gespeichert ist
                if (this.familiensituation.timestampErstellt) {
                    this.fallRS.update(this.gesuchForm.fall); //todo imanol id holen und dem gesuch geben
                    this.gesuchRS.update(this.gesuchForm.gesuch);//todo imanol id holen und der Familiensituation geben
                    this.familiensituationRS.update(this.familiensituation);
                } else {
                    //todo team. Fall und Gesuch sollten in ihren eigenen Services gespeichert werden
                    this.fallRS.create(this.gesuchForm.fall).then((fallResponse: any) => {
                        this.gesuchForm.fall = fallResponse.data;
                        this.gesuchForm.gesuch.fall = fallResponse.data;
                        this.gesuchRS.create(this.gesuchForm.gesuch).then((gesuchResponse: any) => {
                            this.gesuchForm.gesuch = gesuchResponse.data;
                            this.familiensituation.gesuch = gesuchResponse.data;
                            this.familiensituationRS.create(this.familiensituation).then((familienResponse: any) => {
                                this.gesuchForm.familiensituation = familienResponse.data;
                                this.state.go("gesuch.stammdaten", {gesuchstellerNumber:1});
                            });
                        });
                    });
                }
            }
        }

        showGesuchstellerKardinalitaet(): boolean {
            return this.familiensituation.familienstatus === TSFamilienstatus.ALLEINERZIEHEND
                || this.familiensituation.familienstatus === TSFamilienstatus.WENIGER_FUENF_JAHRE;
        }

    }

    angular.module('ebeguWeb.gesuch').component('familiensituationView', new FamiliensituationViewComponentConfig());

}
