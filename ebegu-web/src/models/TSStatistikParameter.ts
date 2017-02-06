import TSAbstractEntity from './TSAbstractEntity';
import TSGesuchsperiode from './TSGesuchsperiode';


export default class TSStatistikParameter extends TSAbstractEntity {
    private _gesuchsperiode: TSGesuchsperiode;
    private _stichtag: moment.Moment;
    private _von: moment.Moment;
    private _bis: moment.Moment;

    constructor( gesuchsperiode?: TSGesuchsperiode, stichtag?: moment.Moment,
                von?: moment.Moment, bis?: moment.Moment) {
        super();
        this._gesuchsperiode = gesuchsperiode;
        this._stichtag = stichtag;
        this._von = von;
        this._bis = bis;
    }

    get gesuchsperiode(): TSGesuchsperiode {
        return this._gesuchsperiode;
    }

    set gesuchsperiode(value: TSGesuchsperiode) {
        this._gesuchsperiode = value;
    }

    get stichtag(): moment.Moment {
        return this._stichtag;
    }

    set stichtag(value: moment.Moment) {
        this._stichtag = value;
    }

    get von(): moment.Moment {
        return this._von;
    }

    set von(value: moment.Moment) {
        this._von = value;
    }

    get bis(): moment.Moment {
        return this._bis;
    }

    set bis(value: moment.Moment) {
        this._bis = (value !== undefined) ? value.add(1, 'days').subtract(1, 'seconds') : value;
    }

}
