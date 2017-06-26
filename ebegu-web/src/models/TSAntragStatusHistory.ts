import TSAbstractEntity from './TSAbstractEntity';
import TSUser from './TSUser';
import {TSAntragStatus} from './enums/TSAntragStatus';
import * as moment from 'moment';

export default class TSAntragStatusHistory extends TSAbstractEntity {

    private _gesuchId: string;
    private _benutzer: TSUser;
    private _timestampVon: moment.Moment;
    private _timestampBis: moment.Moment;
    private _status: TSAntragStatus;


    constructor(gesuchId?: string, benutzer?: TSUser, timestampVon?: moment.Moment, timestampBis?: moment.Moment, status?: TSAntragStatus) {
        super();
        this._gesuchId = gesuchId;
        this._benutzer = benutzer;
        this._timestampVon = timestampVon;
        this._timestampBis = timestampBis;
        this._status = status;
    }


    get gesuchId(): string {
        return this._gesuchId;
    }

    set gesuchId(value: string) {
        this._gesuchId = value;
    }

    get benutzer(): TSUser {
        return this._benutzer;
    }

    set benutzer(value: TSUser) {
        this._benutzer = value;
    }

    get timestampVon(): moment.Moment {
        return this._timestampVon;
    }

    set timestampVon(value: moment.Moment) {
        this._timestampVon = value;
    }

    get timestampBis(): moment.Moment {
        return this._timestampBis;
    }

    set timestampBis(value: moment.Moment) {
        this._timestampBis = value;
    }

    get status(): TSAntragStatus {
        return this._status;
    }

    set status(value: TSAntragStatus) {
        this._status = value;
    }
}
