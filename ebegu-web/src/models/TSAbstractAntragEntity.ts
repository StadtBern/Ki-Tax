import TSFall from './TSFall';
import TSAbstractEntity from './TSAbstractEntity';
import TSGesuchsperiode from './TSGesuchsperiode';

export default class TSAbstractAntragEntity extends TSAbstractEntity {

    private _fall: TSFall;
    private _gesuchsperiode: TSGesuchsperiode;
    private _eingangsdatum: moment.Moment;


    constructor(fall?: TSFall, gesuchsperiode?: TSGesuchsperiode, eingangsdatum?: moment.Moment) {
        super();
        this._fall = fall;
        this._gesuchsperiode = gesuchsperiode;
        this._eingangsdatum = eingangsdatum;
    }


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
}
