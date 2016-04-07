/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.API {
    'use strict';

    export class TSGesuch extends TSAbstractEntity {
        private _fall: TSFall;
        private _gesuchssteller1: TSPerson;
        private _gesuchssteller2: TSPerson;


        public get fall():TSFall {
            return this._fall;
        }

        public set fall(value:TSFall) {
            this._fall = value;
        }

        public get gesuchssteller1():TSPerson {
            return this._gesuchssteller1;
        }

        public set gesuchssteller1(value:TSPerson) {
            this._gesuchssteller1 = value;
        }

        public get gesuchssteller2():TSPerson {
            return this._gesuchssteller2;
        }

        public set gesuchssteller2(value:TSPerson) {
            this._gesuchssteller2 = value;
        }
    }
}
