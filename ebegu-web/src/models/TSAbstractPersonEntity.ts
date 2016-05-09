import TSAbstractEntity from './TSAbstractEntity';
import {TSGeschlecht} from './enums/TSGeschlecht';

export default class TSAbstractPersonEntity extends TSAbstractEntity {

    private _vorname: string;
    private _nachname: string;
    private _geburtsdatum: moment.Moment;
    private _geschlecht: TSGeschlecht;

    constructor(vorname?: string, nachname?: string, geburtsdatum?: moment.Moment, geschlecht?: TSGeschlecht) {
        super();
        this._vorname = vorname;
        this._nachname = nachname;
        this._geburtsdatum = geburtsdatum;
        this.geschlecht = geschlecht;
    }

    public get vorname(): string {
        return this._vorname;
    }

    public set vorname(value: string) {
        this._vorname = value;
    }

    public get nachname(): string {
        return this._nachname;
    }

    public set nachname(value: string) {
        this._nachname = value;
    }

    public get geburtsdatum(): moment.Moment {
        return this._geburtsdatum;
    }

    public set geburtsdatum(value: moment.Moment) {
        this._geburtsdatum = value;
    }

    public get geschlecht(): TSGeschlecht {
        return this._geschlecht;
    }

    public set geschlecht(value: TSGeschlecht) {
        this._geschlecht = value;
    }
}
