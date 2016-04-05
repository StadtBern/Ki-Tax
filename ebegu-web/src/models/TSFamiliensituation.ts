/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.API {
    'use strict';
    import TSFamilienstatus = ebeguWeb.API.TSFamilienstatus;
    import TSGesuchKardinalitaet = ebeguWeb.API.TSGesuchKardinalitaet;

    export class TSFamiliensituation extends TSAbstractEntity {

        private _familienstatus: TSFamilienstatus;
        private _gesuchKardinalitaet: TSGesuchKardinalitaet;
        private _bemerkungen: string;


        constructor(familienstatus?:TSFamilienstatus, gesuchKardinalitaet?: TSGesuchKardinalitaet, bemerkungen?: string) {
            super();
            this._familienstatus = familienstatus;
            this._gesuchKardinalitaet = gesuchKardinalitaet;
            this._bemerkungen = bemerkungen;
        }

        public get familienstatus():TSFamilienstatus {
            return this._familienstatus;
        }

        public set familienstatus(familienstatus:TSFamilienstatus) {
            this._familienstatus = familienstatus;
        }

        public get gesuchKardinalitaet():TSGesuchKardinalitaet {
            return this._gesuchKardinalitaet;
        }

        public set gesuchKardinalitaet(gesuchKardinalitaet:TSGesuchKardinalitaet) {
            this._gesuchKardinalitaet = gesuchKardinalitaet;
        }

        public get bemerkungen():string {
            return this._bemerkungen;
        }

        public set bemerkungen(bemerkungen:string) {
            this._bemerkungen = bemerkungen;
        }
    }

}
