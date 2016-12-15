export default class TSAbstractEntity {
    private _id: string;
    private _timestampErstellt: moment.Moment;
    private _timestampMutiert: moment.Moment;
    private _vorgaengerId: string;


    public set id(id: string) {
        this._id = id;
    }

    public set timestampErstellt(timestampErstellt: moment.Moment) {
        this._timestampErstellt = timestampErstellt;
    }

    public set timestampMutiert(timestampMutiert: moment.Moment) {
        this._timestampMutiert = timestampMutiert;
    }

    public get id(): string {
        return this._id;
    }

    public get timestampErstellt(): moment.Moment {
        return this._timestampErstellt;
    }

    public get timestampMutiert(): moment.Moment {
        return this._timestampMutiert;
    }

    get vorgaengerId(): string {
        return this._vorgaengerId;
    }

    set vorgaengerId(value: string) {
        this._vorgaengerId = value;
    }

    public isNew(): boolean {
        return !this._timestampErstellt;
    }

    public hasVorgaenger(): boolean {
        if (this.vorgaengerId !== null && this.vorgaengerId !== undefined) {
            return true;
        }
        return false;
    }
}
