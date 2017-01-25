import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';
import {TSDateRange} from './types/TSDateRange';

export default class TSGesuchsperiode extends TSAbstractDateRangedEntity {

    private _active: boolean;

    constructor(active?: boolean, gueltigkeit?: TSDateRange) {
        super(gueltigkeit);
        this._active = active;
    }


    get active(): boolean {
        return this._active;
    }

    set active(value: boolean) {
        this._active = value;
    }

    get gesuchsperiodeString(): string {
        if (this.gueltigkeit && this.gueltigkeit.gueltigAb && this.gueltigkeit.gueltigBis) {
            return this.gueltigkeit.gueltigAb.year() + '/'
                + (this.gueltigkeit.gueltigBis.year() - 2000);
        }
        return undefined;
    }
}
