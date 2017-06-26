import TSAbstractEntity from './TSAbstractEntity';
import TSGesuch from './TSGesuch';
import {TSMahnungTyp} from './enums/TSMahnungTyp';
import * as moment from 'moment';

export default class TSMahnung extends TSAbstractEntity {

    private _gesuch: TSGesuch;
    private _mahnungTyp: TSMahnungTyp;
    private _datumFristablauf: moment.Moment;
    private _bemerkungen: string;
    private _timestampAbgeschlossen: moment.Moment;
    private _abgelaufen: boolean;

    constructor(gesuch?: TSGesuch, mahnungTyp?: TSMahnungTyp, datumFristablauf?: moment.Moment, bemerkungen?: string,
                timestampAbgeschlossen?: moment.Moment, abgelaufen?: boolean) {
        super();
        this._gesuch = gesuch;
        this._mahnungTyp = mahnungTyp;
        this._datumFristablauf = datumFristablauf;
        this._bemerkungen = bemerkungen;
        this._timestampAbgeschlossen = timestampAbgeschlossen;
        this._abgelaufen = abgelaufen;
    }

    get gesuch(): TSGesuch {
        return this._gesuch;
    }

    set gesuch(value: TSGesuch) {
        this._gesuch = value;
    }

    get mahnungTyp(): TSMahnungTyp {
        return this._mahnungTyp;
    }

    set mahnungTyp(value: TSMahnungTyp) {
        this._mahnungTyp = value;
    }

    get datumFristablauf(): moment.Moment {
        return this._datumFristablauf;
    }

    set datumFristablauf(value: moment.Moment) {
        this._datumFristablauf = value;
    }

    get bemerkungen(): string {
        return this._bemerkungen;
    }

    set bemerkungen(value: string) {
        this._bemerkungen = value;
    }

    get timestampAbgeschlossen(): moment.Moment {
        return this._timestampAbgeschlossen;
    }

    set timestampAbgeschlossen(value: moment.Moment) {
        this._timestampAbgeschlossen = value;
    }

    get abgelaufen(): boolean {
        return this._abgelaufen;
    }

    set abgelaufen(value: boolean) {
        this._abgelaufen = value;
    }
}
