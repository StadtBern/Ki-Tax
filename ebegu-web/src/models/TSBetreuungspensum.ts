import {TSAbstractPensumEntity} from './TSAbstractPensumEntity';
import {TSDateRange} from './types/TSDateRange';

export default class TSBetreuungspensum extends TSAbstractPensumEntity {

    private _nichtEingetreten: boolean;

    get nichtEingetreten(): boolean {
        return this._nichtEingetreten;
    }

    set nichtEingetreten(value: boolean) {
        this._nichtEingetreten = value;
    }

    constructor(pensum?: number, gueltigkeit?: TSDateRange) {
        super(pensum, gueltigkeit);
    }
}
