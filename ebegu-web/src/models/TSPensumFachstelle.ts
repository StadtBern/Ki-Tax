import {TSAbstractPensumEntity} from './TSAbstractPensumEntity';
import {TSFachstelle} from './TSFachstelle';
import {TSDateRange} from './types/TSDateRange';

export class TSPensumFachstelle extends TSAbstractPensumEntity {

    private _fachstelle: TSFachstelle;

    constructor(fachstelle?: TSFachstelle, pensum?: number, gueltigkeit?: TSDateRange) {
        super(pensum, gueltigkeit);
        this._fachstelle = fachstelle;
    }

    get fachstelle(): TSFachstelle {
        return this._fachstelle;
    }

    set fachstelle(value: TSFachstelle) {
        this._fachstelle = value;
    }
}
