import TSAbstractEntity from './TSAbstractEntity';
import {TSInstitution} from './TSInstitution';
import {TSBetreuungsangebotTyp} from './enums/TSBetreuungsangebotTyp';

export class TSInstitutionStammdaten extends TSAbstractEntity {

    private _iban: string;
    private _oeffnungstage: number;
    private _oeffnungsstunden: number;
    private _betreuungsangebotTyp: TSBetreuungsangebotTyp;
    private _institution: TSInstitution;
    private _gueltigAb: moment.Moment;
    private _gueltigBis: moment.Moment;

    constructor(iban?: string, oeffnungstage?: number, oeffnungsstunden?: number, betreuungsangebotTyp?: TSBetreuungsangebotTyp, institution?: TSInstitution,
                gueltigAb?: moment.Moment, gueltigBis?: moment.Moment) {
        super();
        this._iban = iban;
        this._oeffnungstage = oeffnungstage;
        this._oeffnungsstunden = oeffnungsstunden;
        this._betreuungsangebotTyp = betreuungsangebotTyp;
        this._institution = institution;
        this._gueltigAb = gueltigAb;
        this._gueltigBis = gueltigBis;
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
