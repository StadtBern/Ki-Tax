import {TSAbstractPensumEntity} from './TSAbstractPensumEntity';
import {TSTaetigkeit} from './enums/TSTaetigkeit';
import {TSZuschlagsgrund} from './enums/TSZuschlagsgrund';
import {TSDateRange} from './types/TSDateRange';

/**
 * Definiert ein Erwerbspensum
 */
export default class TSErwerbspensum extends TSAbstractPensumEntity {

    private _taetigkeit: TSTaetigkeit;

    private _zuschlagZuErwerbspensum: boolean;

    private _zuschlagsgrund: TSZuschlagsgrund;

    private _zuschlagsprozent: number;

    private _gesundheitlicheEinschraenkungen: boolean;

    private _bezeichnung: String;


    constructor(pensum?: number, gueltigkeit?: TSDateRange, taetigkeit?: TSTaetigkeit, zuschlagZuErwerbspensum?: boolean,
                zuschlagsgrund?: TSZuschlagsgrund, zuschlagsprozent?: number, gesundheitlicheEinschraenkungen?: boolean) {
        super(pensum, gueltigkeit);
        this._taetigkeit = taetigkeit;
        this._zuschlagZuErwerbspensum = zuschlagZuErwerbspensum;
        this._zuschlagsgrund = zuschlagsgrund;
        this._zuschlagsprozent = zuschlagsprozent;
        this._gesundheitlicheEinschraenkungen = gesundheitlicheEinschraenkungen;
    }


    get taetigkeit(): TSTaetigkeit {
        return this._taetigkeit;
    }

    set taetigkeit(value: TSTaetigkeit) {
        this._taetigkeit = value;
    }

    get zuschlagZuErwerbspensum(): boolean {
        return this._zuschlagZuErwerbspensum;
    }

    set zuschlagZuErwerbspensum(value: boolean) {
        this._zuschlagZuErwerbspensum = value;
    }

    get zuschlagsgrund(): TSZuschlagsgrund {
        return this._zuschlagsgrund;
    }

    set zuschlagsgrund(value: TSZuschlagsgrund) {
        this._zuschlagsgrund = value;
    }

    get zuschlagsprozent(): number {
        return this._zuschlagsprozent;
    }

    set zuschlagsprozent(value: number) {
        this._zuschlagsprozent = value;
    }

    get gesundheitlicheEinschraenkungen(): boolean {
        return this._gesundheitlicheEinschraenkungen;
    }

    set gesundheitlicheEinschraenkungen(value: boolean) {
        this._gesundheitlicheEinschraenkungen = value;
    }

    get bezeichnung(): String {
        return this._bezeichnung;
    }

    set bezeichnung(value: String) {
        this._bezeichnung = value;
    }
}
