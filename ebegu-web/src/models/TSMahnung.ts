import TSAbstractEntity from './TSAbstractEntity';
import TSGesuch from './TSGesuch';
import {TSMahnungTyp} from './enums/TSMahnungTyp';

export default class TSMahnung extends TSAbstractEntity {

    private _gesuch: TSGesuch;
    private _mahnungTyp: TSMahnungTyp;
    private _datumFristablauf: moment.Moment;
    private _bemerkungen: string;
    private _active: boolean;

    constructor(gesuch?: TSGesuch, mahnungTyp?: TSMahnungTyp, datumFristablauf?: moment.Moment, bemerkungen?: string, active?: boolean) {
        super();
        this._gesuch = gesuch;
        this._mahnungTyp = mahnungTyp;
        this._datumFristablauf = datumFristablauf;
        this._bemerkungen = bemerkungen;
        this._active = active;
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

    get active(): boolean {
        return this._active;
    }

    set active(value: boolean) {
        this._active = value;
    }
}
