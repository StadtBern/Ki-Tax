import {TSDateRange} from './types/TSDateRange';
import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';

export default class TSAbwesenheit extends TSAbstractDateRangedEntity {

    constructor(gueltigkeit?: TSDateRange) {
        super(gueltigkeit);
    }
}
