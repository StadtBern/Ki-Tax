import TSAbstractEntity from './TSAbstractEntity';
import TSFinanzielleSituation from './TSFinanzielleSituation';

export default class TSFinanzielleSituationContainer extends TSAbstractEntity {

    private _jahr: number;
    private _finanzielleSituationGS: TSFinanzielleSituation;
    private _finanzielleSituationJA: TSFinanzielleSituation;
    private _finanzielleSituationSV: TSFinanzielleSituation;

    constructor(jahr?: number, finanzielleSituationGS?: TSFinanzielleSituation,
                finanzielleSituationJA?: TSFinanzielleSituation,
                finanzielleSituationSV?: TSFinanzielleSituation) {
        super();
        this._jahr = jahr;
        this._finanzielleSituationGS = finanzielleSituationGS;
        this._finanzielleSituationJA = finanzielleSituationJA;
        this._finanzielleSituationSV = finanzielleSituationSV;
    }

    get jahr(): number {
        return this._jahr;
    }

    set jahr(value: number) {
        this._jahr = value;
    }

    get finanzielleSituationGS(): TSFinanzielleSituation {
        return this._finanzielleSituationGS;
    }

    set finanzielleSituationGS(value: TSFinanzielleSituation) {
        this._finanzielleSituationGS = value;
    }

    get finanzielleSituationJA(): TSFinanzielleSituation {
        return this._finanzielleSituationJA;
    }

    set finanzielleSituationJA(value: TSFinanzielleSituation) {
        this._finanzielleSituationJA = value;
    }

    get finanzielleSituationSV(): TSFinanzielleSituation {
        return this._finanzielleSituationSV;
    }

    set finanzielleSituationSV(value: TSFinanzielleSituation) {
        this._finanzielleSituationSV = value;
    }
}
