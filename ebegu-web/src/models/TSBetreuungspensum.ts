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

    constructor(nichtEingetreten?: boolean, pensum?: number, gueltigkeit?: TSDateRange) {
        super(pensum, gueltigkeit);
        this.nichtEingetreten = nichtEingetreten;
    }
}
