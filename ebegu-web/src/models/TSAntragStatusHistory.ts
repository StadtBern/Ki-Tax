import TSAbstractEntity from './TSAbstractEntity';
import TSUser from './TSUser';
import {TSAntragStatus} from './enums/TSAntragStatus';

export default class TSAntragStatusHistory extends TSAbstractEntity {

    private _gesuchId: string;
    private _benutzer: TSUser;
    private _datum: moment.Moment;
    private _status: TSAntragStatus;


    constructor(gesuchId?: string, benutzer?: TSUser, datum?: moment.Moment, status?: TSAntragStatus) {
        super();
        this._gesuchId = gesuchId;
        this._benutzer = benutzer;
        this._datum = datum;
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

    get datum(): moment.Moment {
        return this._datum;
    }

    set datum(value: moment.Moment) {
        this._datum = value;
    }

    get status(): TSAntragStatus {
        return this._status;
    }

    set status(value: TSAntragStatus) {
        this._status = value;
    }
}
