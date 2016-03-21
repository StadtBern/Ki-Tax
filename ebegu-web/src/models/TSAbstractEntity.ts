/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.API {
    'use strict';

    export class TSAbstractEntity {
        _id: string;
        _timestampErstellt: moment.Moment;
        _timestampMutiert: moment.Moment;


        public set id(id:string) {
            this._id = id;
        }

        public set timestampErstellt(timestampErstellt: moment.Moment) {
            this._timestampErstellt = timestampErstellt;
        }

        public set timestampMutiert(timestampMutiert: moment.Moment) {
            this._timestampMutiert = timestampMutiert;
        }

        public get id():string {
            return this._id;
        }

        public get timestampErstellt(): moment.Moment {
            return this._timestampErstellt;
        }

        public get timestampMutiert(): moment.Moment {
            return this._timestampMutiert;
        }
    }
}
