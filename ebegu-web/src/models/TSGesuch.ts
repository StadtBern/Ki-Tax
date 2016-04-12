import TSAbstractEntity from './TSAbstractEntity';

export default class TSGesuch extends TSAbstractEntity {

    private _familiensituation: string;
    private _beantragen: string;
    private _bemerkungen: string;


    constructor(familiensituation?: string, beantragen?: string, bemerkungen?: string) {
        super();
        this._familiensituation = familiensituation;
        this._beantragen = beantragen;
        this._bemerkungen = bemerkungen;
    }

    public get familiensituation(): string {
        return this._familiensituation;
    }

    public set familiensituation(familiensituation: string) {
        this._familiensituation = familiensituation;
    }

    public get beantragen(): string {
        return this._beantragen;
    }

    public set beantragen(beantragen: string) {
        this._beantragen = beantragen;
    }

    public get bemerkungen(): string {
        return this._bemerkungen;
    }

    public set bemerkungen(bemerkungen: string) {
        this._bemerkungen = bemerkungen;
    }
}
