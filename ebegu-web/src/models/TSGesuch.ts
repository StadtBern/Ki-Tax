/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.API {
    'use strict';

    export class TSGesuch extends TSAbstractEntity {
        private _fall: TSFall;
        private _gesuchsteller1: TSPerson;
        private _gesuchsteller2: TSPerson;


        public get fall():TSFall {
            return this._fall;
        }

        public set fall(value:TSFall) {
            this._fall = value;
        }

        public get gesuchsteller1():TSPerson {
            return this._gesuchsteller1;
        }

        public set gesuchsteller1(value:TSPerson) {
            this._gesuchsteller1 = value;
        }

        public get gesuchsteller2():TSPerson {
            return this._gesuchsteller2;
        }

        public set gesuchsteller2(value:TSPerson) {
            this._gesuchsteller2 = value;
        }
    }
}
