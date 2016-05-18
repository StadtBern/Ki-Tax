import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';
import {TSDateRange} from './types/TSDateRange';

export class TSAbstractPensumEntity extends TSAbstractDateRangedEntity {

    private _pensum: number;

    constructor(pensum?: number, gueltigkeit?: TSDateRange) {
        super(gueltigkeit);
        this._pensum = pensum;
    }


    get pensum(): number {
        return this._pensum;
    }

    set pensum(value: number) {
        this._pensum = value;
    }
}
