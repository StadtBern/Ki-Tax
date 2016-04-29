import TSAbstractEntity from './TSAbstractEntity';
import {TSDateRange} from './types/TSDateRange';

export class TSAbstractDateRangedEntity extends TSAbstractEntity {

    private _gueltigkeit: TSDateRange;

    constructor(gueltigkeit?: TSDateRange) {
        super();
        this._gueltigkeit = gueltigkeit;
    }

    get gueltigkeit(): TSDateRange {
        return this._gueltigkeit;
    }

    set gueltigkeit(value: TSDateRange) {
        this._gueltigkeit = value;
    }
}
