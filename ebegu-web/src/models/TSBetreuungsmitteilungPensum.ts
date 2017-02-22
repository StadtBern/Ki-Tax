import {TSAbstractPensumEntity} from './TSAbstractPensumEntity';
import {TSDateRange} from './types/TSDateRange';

export default class TSBetreuungsmitteilungPensum extends TSAbstractPensumEntity {

    constructor(pensum?: number, gueltigkeit?: TSDateRange) {
        super(pensum, gueltigkeit);
    }
}
