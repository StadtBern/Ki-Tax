import TSAbstractEntity from './TSAbstractEntity';
import TSEinkommensverschlechterung from './TSEinkommensverschlechterung';


export default class TSEinkommensverschlechterungContainer extends TSAbstractEntity {

    private _ekvGSBasisJahrPlus1: TSEinkommensverschlechterung;
    private _ekvGSBasisJahrPlus2: TSEinkommensverschlechterung;
    private _ekvJABasisJahrPlus1: TSEinkommensverschlechterung;
    private _ekvJABasisJahrPlus2: TSEinkommensverschlechterung;

    constructor(ekvGSBasisJahrPlus1?: TSEinkommensverschlechterung,
                ekvGSBasisJahrPlus2?: TSEinkommensverschlechterung,
                ekvJABasisJahrPlus1?: TSEinkommensverschlechterung,
                ekvJABasisJahrPlus2?: TSEinkommensverschlechterung) {
        super();
        this._ekvGSBasisJahrPlus1 = ekvGSBasisJahrPlus1;
        this._ekvGSBasisJahrPlus2 = ekvGSBasisJahrPlus2;
        this._ekvJABasisJahrPlus1 = ekvJABasisJahrPlus1;
        this._ekvJABasisJahrPlus2 = ekvJABasisJahrPlus2;
    }


    get ekvGSBasisJahrPlus1(): TSEinkommensverschlechterung {
        return this._ekvGSBasisJahrPlus1;
    }

    set ekvGSBasisJahrPlus1(value: TSEinkommensverschlechterung) {
        this._ekvGSBasisJahrPlus1 = value;
    }

    get ekvGSBasisJahrPlus2(): TSEinkommensverschlechterung {
        return this._ekvGSBasisJahrPlus2;
    }

    set ekvGSBasisJahrPlus2(value: TSEinkommensverschlechterung) {
        this._ekvGSBasisJahrPlus2 = value;
    }

    get ekvJABasisJahrPlus1(): TSEinkommensverschlechterung {
        return this._ekvJABasisJahrPlus1;
    }

    set ekvJABasisJahrPlus1(value: TSEinkommensverschlechterung) {
        this._ekvJABasisJahrPlus1 = value;
    }

    get ekvJABasisJahrPlus2(): TSEinkommensverschlechterung {
        return this._ekvJABasisJahrPlus2;
    }

    set ekvJABasisJahrPlus2(value: TSEinkommensverschlechterung) {
        this._ekvJABasisJahrPlus2 = value;
    }
}
