import TSFall from './TSFall';
import TSAbstractEntity from './TSAbstractEntity';
import TSGesuchsperiode from './TSGesuchsperiode';
import {TSAntragStatus} from './enums/TSAntragStatus';
import {TSAntragTyp} from './enums/TSAntragTyp';
import {TSEingangsart} from './enums/TSEingangsart';
import * as moment from 'moment';

export default class TSAbstractAntragEntity extends TSAbstractEntity {


    private _fall: TSFall;
    private _gesuchsperiode: TSGesuchsperiode;
    private _eingangsdatum: moment.Moment;
    private _freigabeDatum: moment.Moment;
    private _status: TSAntragStatus;
    private _typ: TSAntragTyp;
    private _eingangsart: TSEingangsart;


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

    get freigabeDatum(): moment.Moment {
        return this._freigabeDatum;
    }

    set freigabeDatum(value: moment.Moment) {
        this._freigabeDatum = value;
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

    get eingangsart(): TSEingangsart {
        return this._eingangsart;
    }

    set eingangsart(value: TSEingangsart) {
        this._eingangsart = value;
    }
}
