import TSAbstractEntity from './TSAbstractEntity';
import TSKind from './TSKind';
import TSBetreuung from './TSBetreuung';

export default class TSKindContainer extends TSAbstractEntity {

    private _kindGS: TSKind;
    private _kindJA: TSKind;
    private _betreuungen: Array<TSBetreuung>;

    constructor(kindGS?: TSKind, kindJA?: TSKind, betreuungen?: Array<TSBetreuung>) {
        super();
        this._kindGS = kindGS;
        this._kindJA = kindJA;
        this._betreuungen = betreuungen ? betreuungen : [];
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

    get betreuungen(): Array<TSBetreuung> {
        return this._betreuungen;
    }

    set betreuungen(value: Array<TSBetreuung>) {
        this._betreuungen = value;
    }
}
