/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.API {
    'use strict';
    import TSFamilienstatus = ebeguWeb.API.TSFamilienstatus;
    import TSGesuchstellerKardinalitaet = ebeguWeb.API.TSGesuchstellerKardinalitaet;
    import TSGesuch = ebeguWeb.API.TSGesuch;

    export class TSFamiliensituation extends TSAbstractEntity {

        private _familienstatus: TSFamilienstatus;
        private _gesuchstellerKardinalitaet: TSGesuchstellerKardinalitaet;
        private _bemerkungen: string;
        private _gesuch: TSGesuch;


        constructor(familienstatus?:TSFamilienstatus, gesuchstellerKardinalitaet?: TSGesuchstellerKardinalitaet, bemerkungen?: string) {
            super();
            this._familienstatus = familienstatus;
            this._gesuchstellerKardinalitaet = gesuchstellerKardinalitaet;
            this._bemerkungen = bemerkungen;
        }

        public get familienstatus():TSFamilienstatus {
            return this._familienstatus;
        }

        public set familienstatus(familienstatus:TSFamilienstatus) {
            this._familienstatus = familienstatus;
        }

        public get gesuchstellerKardinalitaet():TSGesuchstellerKardinalitaet {
            return this._gesuchstellerKardinalitaet;
        }

        public set gesuchstellerKardinalitaet(gesuchstellerKardinalitaet:TSGesuchstellerKardinalitaet) {
            this._gesuchstellerKardinalitaet = gesuchstellerKardinalitaet;
        }

        public get bemerkungen():string {
            return this._bemerkungen;
        }

        public set bemerkungen(bemerkungen:string) {
            this._bemerkungen = bemerkungen;
        }

        public get gesuch():TSGesuch {
            return this._gesuch;
        }

        public set gesuch(value:TSGesuch) {
            this._gesuch = value;
        }
    }

}
