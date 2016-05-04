import TSAbstractEntity from './TSAbstractEntity';
import TSKind from './TSKind';

export default class TSKindContainer extends TSAbstractEntity {

    private _kindGS: TSKind;
    private _kindJA: TSKind;

    constructor(kindGS?: TSKind, kindJA?: TSKind) {
        super();
        this._kindGS = kindGS;
        this._kindJA = kindJA;
    }


    get kindGS(): TSKind {
        return this._kindGS;
    }

    set kindGS(value: TSKind) {
        this._kindGS = value;
    }

    get kindJA(): TSKind {
        return this._kindJA;
    }

    set kindJA(value: TSKind) {
        this._kindJA = value;
    }
}
