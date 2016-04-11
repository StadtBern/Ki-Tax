/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.services {
    import TSFall = ebeguWeb.API.TSFall;
    import TSGesuch = ebeguWeb.API.TSGesuch;
    import TSFamiliensituation = ebeguWeb.API.TSFamiliensituation;
    import TSFamilienstatus = ebeguWeb.API.TSFamilienstatus;
    import TSGesuchstellerKardinalitaet = ebeguWeb.API.TSGesuchstellerKardinalitaet;
    'use strict';

    export interface IGesuchForm {
        fall: ebeguWeb.API.TSFall;
        gesuch: ebeguWeb.API.TSGesuch;
        familiensituation: TSFamiliensituation;
    }

    export class GesuchForm implements IGesuchForm {
        fall:ebeguWeb.API.TSFall;
        gesuch:ebeguWeb.API.TSGesuch;
        familiensituation: ebeguWeb.API.TSFamiliensituation;

        static $inject = [];
        /* @ngInject */
        constructor() {
            this.fall = new TSFall();
            this.gesuch = new TSGesuch();
            this.familiensituation = new TSFamiliensituation();
        }

        /**
         * Prueft ob der 2. Gesuchtsteller eingetragen werden muss je nach dem was in Familiensituation ausgewaehlt wurde
         * @returns {boolean} False wenn "Alleinerziehend" oder "weniger als 5 Jahre" und dazu "alleine" ausgewaehlt wurde.
         */
        public isGesuchsteller2Required():boolean {
            return !(((this.familiensituation.familienstatus === TSFamilienstatus.ALLEINERZIEHEND) || (this.familiensituation.familienstatus === TSFamilienstatus.WENIGER_FUENF_JAHRE))
                && (this.familiensituation.gesuchstellerKardinalitaet === TSGesuchstellerKardinalitaet.ALLEINE));

        }

        static instance () : IGesuchForm {
            return new GesuchForm();
        }

    }

    angular.module('ebeguWeb.core').factory('gesuchForm', GesuchForm.instance);

}
