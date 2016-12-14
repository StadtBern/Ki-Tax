import TSAbstractEntity from './TSAbstractEntity';
import TSFamiliensituation from './TSFamiliensituation';

export default class TSFamiliensituationContainer extends TSAbstractEntity {

    private _familiensituationJA: TSFamiliensituation;
    private _familiensituationGS: TSFamiliensituation;
    private _familiensituationErstgesuch: TSFamiliensituation;


    constructor(familiensituationJA?: TSFamiliensituation, familiensituationGS?: TSFamiliensituation,
                familiensituationErstgesuch?: TSFamiliensituation) {
        super();
        this._familiensituationJA = familiensituationJA;
        this._familiensituationGS = familiensituationGS;
        this._familiensituationErstgesuch = familiensituationErstgesuch;
    }

    get familiensituationJA(): TSFamiliensituation {
        return this._familiensituationJA;
    }

    set familiensituationJA(value: TSFamiliensituation) {
        this._familiensituationJA = value;
    }

    get familiensituationGS(): TSFamiliensituation {
        return this._familiensituationGS;
    }

    set familiensituationGS(value: TSFamiliensituation) {
        this._familiensituationGS = value;
    }

    get familiensituationErstgesuch(): TSFamiliensituation {
        return this._familiensituationErstgesuch;
    }

    set familiensituationErstgesuch(value: TSFamiliensituation) {
        this._familiensituationErstgesuch = value;
    }
}
