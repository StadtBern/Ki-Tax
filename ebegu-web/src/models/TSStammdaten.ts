/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.API {

    export class TSStammdaten extends TSAbstractEntity {

        private _vorname:string;
        private _nachname:string;
        private _geburtsdatum:moment.Moment;
        private _email:string;
        private _mobile:string;
        private _telefon:string;
        private _telefonAusland:string;
        private _umzug:boolean;
        private _geschlecht:ebeguWeb.API.TSGeschlecht;
        private _adresse:ebeguWeb.API.TSAdresse;
        private _umzugadresse:ebeguWeb.API.TSAdresse;

        constructor(vorname?:string, nachname?:string, geburtsdatum?:moment.Moment, email?:string, mobile?:string,
                    telefon?:string, telefonAusland?:string, umzug?:boolean) {
            super();
            this._vorname = vorname;
            this._nachname = nachname;
            this._geburtsdatum = geburtsdatum;
            this._email = email;
            this._mobile = mobile;
            this._telefon = telefon;
            this._telefonAusland = telefonAusland;
            this._umzug = umzug;
        }


        public get vorname():string {
            return this._vorname;
        }

        public set vorname(value:string) {
            this._vorname = value;
        }

        public get nachname():string {
            return this._nachname;
        }

        public set nachname(value:string) {
            this._nachname = value;
        }

        public get geburtsdatum():moment.Moment {
            return this._geburtsdatum;
        }

        public set geburtsdatum(value:moment.Moment) {
            this._geburtsdatum = value;
        }

        public get email():string {
            return this._email;
        }

        public set email(value:string) {
            this._email = value;
        }

        public get mobile():string {
            return this._mobile;
        }

        public set mobile(value:string) {
            this._mobile = value;
        }

        public get telefon():string {
            return this._telefon;
        }

        public set telefon(value:string) {
            this._telefon = value;
        }

        public get umzug():boolean {
            return this._umzug;
        }

        public set umzug(value:boolean) {
            this._umzug = value;
        }


        public get adresse():ebeguWeb.API.TSAdresse {
            return this._adresse;
        }

        public set adresse(adr:ebeguWeb.API.TSAdresse) {
            this._adresse = adr;
        }


        public get geschlecht():ebeguWeb.API.TSGeschlecht {
            return this._geschlecht;
        }

        public set geschlecht(value:ebeguWeb.API.TSGeschlecht) {
            this._geschlecht = value;
        }


        public get umzugadresse():ebeguWeb.API.TSAdresse {
            return this._umzugadresse;
        }

        public set umzugadresse(value:ebeguWeb.API.TSAdresse) {
            this._umzugadresse = value;
        }


        public get telefonAusland():string {
            return this._telefonAusland;
        }

        public set telefonAusland(value:string) {
            this._telefonAusland = value;
        }
    }
}
