import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';
import {TSDateRange} from './types/TSDateRange';
import {TSEbeguVorlageKey} from './enums/TSEbeguVorlageKey';
import TSVorlage from './TSVorlage';

export default class TSEbeguVorlage extends TSAbstractDateRangedEntity {

    private _name: TSEbeguVorlageKey;
    private _vorlage: TSVorlage;
    private _proGesuchsperiode: boolean;

    constructor(name?: TSEbeguVorlageKey, vorlage?: TSVorlage, gueltigkeit?: TSDateRange, proGesuchsperiode?: boolean) {
        super(gueltigkeit);
        this._name = name;
        this._vorlage = vorlage;
        this._proGesuchsperiode = proGesuchsperiode;
    }

    get name(): TSEbeguVorlageKey {
        return this._name;
    }

    set name(value: TSEbeguVorlageKey) {
        this._name = value;
    }

    get vorlage(): TSVorlage {
        return this._vorlage;
    }

    set vorlage(value: TSVorlage) {
        this._vorlage = value;
    }

    get proGesuchsperiode(): boolean {
        return this._proGesuchsperiode;
    }

    set proGesuchsperiode(value: boolean) {
        this._proGesuchsperiode = value;
    }
}
