import TSFall from './TSFall';
import TSAbstractEntity from './TSAbstractEntity';
import TSGesuchsperiode from './TSGesuchsperiode';
import {TSAntragStatus} from './enums/TSAntragStatus';
import {TSAntragTyp} from './enums/TSAntragTyp';

export default class TSAbstractAntragEntity extends TSAbstractEntity {


    private _fall: TSFall;
    private _gesuchsperiode: TSGesuchsperiode;
    private _eingangsdatum: moment.Moment;
    private _status: TSAntragStatus;
    private _typ: TSAntragTyp;


    public get fall(): TSFall {
        return this._fall;
    }

    public set fall(value: TSFall) {
        this._fall = value;
    }

    get gesuchsperiode(): TSGesuchsperiode {
        return this._gesuchsperiode;
    }

    set gesuchsperiode(gesuchsperiode: TSGesuchsperiode) {
        this._gesuchsperiode = gesuchsperiode;
    }

    get eingangsdatum(): moment.Moment {
        return this._eingangsdatum;
    }

    set eingangsdatum(value: moment.Moment) {
        this._eingangsdatum = value;
    }

    get status(): TSAntragStatus {
        return this._status;
    }

    set status(value: TSAntragStatus) {
        this._status = value;
    }

    get typ(): TSAntragTyp {
        return this._typ;
    }

    set typ(value: TSAntragTyp) {
        this._typ = value;
    }
}
