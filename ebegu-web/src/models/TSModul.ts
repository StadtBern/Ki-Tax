import TSAbstractEntity from './TSAbstractEntity';
import {TSModulname} from './enums/TSModulname';
import {TSDayOfWeek} from './enums/TSDayOfWeek';
import * as moment from 'moment';

export default class TSModul extends TSAbstractEntity {

    private _wochentag: TSDayOfWeek;
    private _modulname: TSModulname;
    private _zeitVon: moment.Moment;
    private _zeitBis: moment.Moment;

    public get wochentag(): TSDayOfWeek {
        return this._wochentag;
    }

    public set wochentag(value: TSDayOfWeek) {
        this._wochentag = value;
    }

    public get modulname(): TSModulname {
        return this._modulname;
    }

    public set modulname(value: TSModulname) {
        this._modulname = value;
    }

    public get zeitVon(): moment.Moment {
        return this._zeitVon;
    }

    public set zeitVon(value: moment.Moment) {
        this._zeitVon = value;
    }

    public get zeitBis(): moment.Moment {
        return this._zeitBis;
    }

    public set zeitBis(value: moment.Moment) {
        this._zeitBis = value;
    }
}
