import TSAbstractEntity from './TSAbstractEntity';
import TSEinkommensverschlechterungInfo from './TSEinkommensverschlechterungInfo';

export default class TSEinkommensverschlechterungInfoContainer extends TSAbstractEntity {

    private _einkommensverschlechterungInfoGS: TSEinkommensverschlechterungInfo;

    private _einkommensverschlechterungInfoJA: TSEinkommensverschlechterungInfo = new TSEinkommensverschlechterungInfo;

    get einkommensverschlechterungInfoGS(): TSEinkommensverschlechterungInfo {
        return this._einkommensverschlechterungInfoGS;
    }

    set einkommensverschlechterungInfoGS(value: TSEinkommensverschlechterungInfo) {
        this._einkommensverschlechterungInfoGS = value;
    }

    get einkommensverschlechterungInfoJA(): TSEinkommensverschlechterungInfo {
        return this._einkommensverschlechterungInfoJA;
    }

    set einkommensverschlechterungInfoJA(value: TSEinkommensverschlechterungInfo) {
        this._einkommensverschlechterungInfoJA = value;
    }

    public init() : void {
        this.einkommensverschlechterungInfoJA = new TSEinkommensverschlechterungInfo();
        this.einkommensverschlechterungInfoJA.ekvFuerBasisJahrPlus1 = false;
        this.einkommensverschlechterungInfoJA.ekvFuerBasisJahrPlus2 = false;
    }
}
