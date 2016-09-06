import {TSFamilienstatus} from './enums/TSFamilienstatus';
import {TSGesuchstellerKardinalitaet} from './enums/TSGesuchstellerKardinalitaet';
import TSAbstractEntity from './TSAbstractEntity';

export default class TSFamiliensituation extends TSAbstractEntity {

    private _familienstatus: TSFamilienstatus;
    private _gesuchstellerKardinalitaet: TSGesuchstellerKardinalitaet;
    private _gemeinsameSteuererklaerung: boolean;


    constructor(familienstatus?: TSFamilienstatus, gesuchstellerKardinalitaet?: TSGesuchstellerKardinalitaet,
                bemerkungen?: string, gemeinsameSteuererklaerung?: boolean) {
        super();
        this._familienstatus = familienstatus;
        this._gesuchstellerKardinalitaet = gesuchstellerKardinalitaet;
        this._gemeinsameSteuererklaerung = gemeinsameSteuererklaerung;
    }

    public get familienstatus(): TSFamilienstatus {
        return this._familienstatus;
    }

    public set familienstatus(familienstatus: TSFamilienstatus) {
        this._familienstatus = familienstatus;
    }

    public get gesuchstellerKardinalitaet(): TSGesuchstellerKardinalitaet {
        return this._gesuchstellerKardinalitaet;
    }

    public set gesuchstellerKardinalitaet(gesuchstellerKardinalitaet: TSGesuchstellerKardinalitaet) {
        this._gesuchstellerKardinalitaet = gesuchstellerKardinalitaet;
    }

    get gemeinsameSteuererklaerung(): boolean {
        return this._gemeinsameSteuererklaerung;
    }

    set gemeinsameSteuererklaerung(value: boolean) {
        this._gemeinsameSteuererklaerung = value;
    }

    // todo team Dieser Code is gleich wie auf dem Server...
    public hasSecondGesuchsteller(): boolean {
        switch (this.familienstatus) {
            case TSFamilienstatus.ALLEINERZIEHEND:
            case TSFamilienstatus.WENIGER_FUENF_JAHRE:
                return TSGesuchstellerKardinalitaet.ZU_ZWEIT === this.gesuchstellerKardinalitaet;
            case TSFamilienstatus.VERHEIRATET:
            case TSFamilienstatus.KONKUBINAT:
            case TSFamilienstatus.LAENGER_FUENF_JAHRE:
                return true;
        }
        //wir sollten hier nie hinkommen
        return false;
    }
}
