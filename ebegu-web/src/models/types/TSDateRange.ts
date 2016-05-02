export class TSDateRange {

    private _gueltigAb: moment.Moment;
    private _gueltigBis: moment.Moment;

    constructor(gueltigAb?: moment.Moment, gueltigBis?: moment.Moment) {
        this._gueltigAb = gueltigAb;
        this._gueltigBis = gueltigBis;
    }

    get gueltigAb(): moment.Moment {
        return this._gueltigAb;
    }

    set gueltigAb(value: moment.Moment) {
        this._gueltigAb = value;
    }

    get gueltigBis(): moment.Moment {
        return this._gueltigBis;
    }

    set gueltigBis(value: moment.Moment) {
        this._gueltigBis = value;
    }
}
