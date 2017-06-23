import TSAbstractEntity from './TSAbstractEntity';
import * as moment from 'moment';


export default class TSStatistikParameter extends TSAbstractEntity {
    private _gesuchsperiode: string;
    private _stichtag: moment.Moment;
    private _von: moment.Moment;
    private _bis: moment.Moment;

    constructor( gesuchsperiode?: string, stichtag?: moment.Moment,
                von?: moment.Moment, bis?: moment.Moment) {
        super();
        this._gesuchsperiode = gesuchsperiode;
        this._stichtag = stichtag;
        this._von = von;
        this._bis = bis;
    }

    get gesuchsperiode(): string {
        return this._gesuchsperiode;
    }

    set gesuchsperiode(value: string) {
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
