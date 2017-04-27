import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';
import {TSDateRange} from './types/TSDateRange';
import {TSGesuchsperiodeStatus} from './enums/TSGesuchsperiodeStatus';

export default class TSGesuchsperiode extends TSAbstractDateRangedEntity {

    private _status: TSGesuchsperiodeStatus;

    constructor(status?: TSGesuchsperiodeStatus, gueltigkeit?: TSDateRange) {
        super(gueltigkeit);
        this._status = status;
    }


    get status(): TSGesuchsperiodeStatus {
        return this._status;
    }

    set status(value: TSGesuchsperiodeStatus) {
        this._status = value;
    }

    get gesuchsperiodeString(): string {
        if (this.gueltigkeit && this.gueltigkeit.gueltigAb && this.gueltigkeit.gueltigBis) {
            return this.gueltigkeit.gueltigAb.year() + '/'
                + (this.gueltigkeit.gueltigBis.year() - 2000);
        }
        return undefined;
    }
}
