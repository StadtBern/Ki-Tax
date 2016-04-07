/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.API {
    'use strict';

    export class TSGesuch extends TSAbstractEntity {
        private _fall: TSFall;


        public get fall():TSFall {
            return this._fall;
        }

        public set fall(value:TSFall) {
            this._fall = value;
        }
    }
}
