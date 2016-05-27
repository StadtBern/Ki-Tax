import TSAbstractEntity from './TSAbstractEntity';
import TSBetreuungspensum from './TSBetreuungspensum';

export default class TSBetreuungspensumContainer extends TSAbstractEntity {

    private _betreuungspensumGS: TSBetreuungspensum;
    private _betreuungspensumJA: TSBetreuungspensum;

    constructor(betreuungspensumGS?: TSBetreuungspensum, betreuungspensumJA?: TSBetreuungspensum) {
        super();
        this._betreuungspensumGS = betreuungspensumGS;
        this._betreuungspensumJA = betreuungspensumJA;
    }

    get betreuungspensumGS(): TSBetreuungspensum {
        return this._betreuungspensumGS;
    }

    set betreuungspensumGS(value: TSBetreuungspensum) {
        this._betreuungspensumGS = value;
    }

    get betreuungspensumJA(): TSBetreuungspensum {
        return this._betreuungspensumJA;
    }

    set betreuungspensumJA(value: TSBetreuungspensum) {
        this._betreuungspensumJA = value;
    }
}
