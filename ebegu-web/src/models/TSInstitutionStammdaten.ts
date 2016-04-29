import {TSInstitution} from './TSInstitution';
import {TSBetreuungsangebotTyp} from './enums/TSBetreuungsangebotTyp';
import {TSDateRange} from './types/TSDateRange';
import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';

export class TSInstitutionStammdaten extends TSAbstractDateRangedEntity {

    private _iban: string;
    private _oeffnungstage: number;
    private _oeffnungsstunden: number;
    private _betreuungsangebotTyp: TSBetreuungsangebotTyp;
    private _institution: TSInstitution;


    constructor(iban?: string, oeffnungstage?: number, oeffnungsstunden?: number, betreuungsangebotTyp?: TSBetreuungsangebotTyp,
                institution?: TSInstitution, gueltigkeit?: TSDateRange) {
        super(gueltigkeit);
        this._iban = iban;
        this._oeffnungstage = oeffnungstage;
        this._oeffnungsstunden = oeffnungsstunden;
        this._betreuungsangebotTyp = betreuungsangebotTyp;
        this._institution = institution;
    }


    public get iban(): string {
        return this._iban;
    }

    public set iban(value: string) {
        this._iban = value;
    }

    public get oeffnungstage(): number {
        return this._oeffnungstage;
    }

    public set oeffnungstage(value: number) {
        this._oeffnungstage = value;
    }

    public get oeffnungsstunden(): number {
        return this._oeffnungsstunden;
    }

    public set oeffnungsstunden(value: number) {
        this._oeffnungsstunden = value;
    }

    public get betreuungsangebotTyp(): TSBetreuungsangebotTyp {
        return this._betreuungsangebotTyp;
    }

    public set betreuungsangebotTyp(value: TSBetreuungsangebotTyp) {
        this._betreuungsangebotTyp = value;
    }

    public get institution(): TSInstitution {
        return this._institution;
    }

    public set institution(value: TSInstitution) {
        this._institution = value;
    }

}
