import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';
import {TSEbeguParameterKey} from './enums/TSEbeguParameterKey';
import {TSDateRange} from './types/TSDateRange';

export default class TSEbeguParameter extends TSAbstractDateRangedEntity {

    private _name: TSEbeguParameterKey;
    private _value: string;
    private _proGesuchsperiode: boolean;

    constructor(name?: TSEbeguParameterKey, value?: string, gueltigkeit?: TSDateRange, proGesuchsperiode?: boolean) {
        super(gueltigkeit);
        this._name = name;
        this._value = value;
        this._proGesuchsperiode = proGesuchsperiode;
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

    get proGesuchsperiode(): boolean {
        return this._proGesuchsperiode;
    }

    set proGesuchsperiode(value: boolean) {
        this._proGesuchsperiode = value;
    }
}
