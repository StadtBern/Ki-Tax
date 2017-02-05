import TSAbstractEntity from './TSAbstractEntity';
import {TSStatistikParameterType} from './enums/TSStatistikParameterType';
import {TSDateRange} from './types/TSDateRange';
import TSGesuchsperiode from './TSGesuchsperiode';


export default class TSStatistikParameter extends TSAbstractEntity {
    private _type: TSStatistikParameterType;
    private _gesuchsperiode: TSGesuchsperiode;
    private _stichtag: moment.Moment;
    private _von: moment.Moment;
    private _bis: moment.Moment;

    constructor(type?: TSStatistikParameterType, gesuchsperiode?: TSGesuchsperiode, stichtag?: moment.Moment,
                von?: moment.Moment, bis?: moment.Moment, statistikparameter?: TSStatistikParameter) {
        super();
        this._type = type || null;
        this._gesuchsperiode = gesuchsperiode || null;
        this._stichtag = stichtag || null;
        this._von = von || null;
        this._bis = bis || null;
    }

    get type(): TSStatistikParameterType {
        return this._type;
    }

    set type(value: TSStatistikParameterType) {
        this._type = value;
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
        this._bis = value;
    }

}
