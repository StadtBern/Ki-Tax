import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';
import {TSEbeguParameterKey} from './enums/TSEbeguParameterKey';
import {TSDateRange} from './types/TSDateRange';

export default class TSEbeguParameter extends TSAbstractDateRangedEntity {

    private _name: TSEbeguParameterKey;
    private _value: string;

    constructor(name?: TSEbeguParameterKey, value?: string, gueltigkeit?: TSDateRange) {
        super(gueltigkeit);
        this._name = name;
        this._value = value;
    }

    public set name(name: TSEbeguParameterKey) {
        this._name = name;
    }

    public set value(value: string) {
        this._value = value;
    }

    public get name(): TSEbeguParameterKey {
        return this._name;
    }

    public get value(): string {
        return this._value;
    }
}
