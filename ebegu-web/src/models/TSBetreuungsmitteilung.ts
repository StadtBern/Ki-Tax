import TSMitteilung from './TSMitteilung';
import TSBetreuungsmitteilungPensum from './TSBetreuungsmitteilungPensum';

export default class TSBetreuungsmitteilung extends TSMitteilung {

    private _betreuungspensen: Array<TSBetreuungsmitteilungPensum>;


    constructor(betreuungspensen?: Array<TSBetreuungsmitteilungPensum>) {
        super();
        this._betreuungspensen = betreuungspensen;
    }


    get betreuungspensen(): Array<TSBetreuungsmitteilungPensum> {
        return this._betreuungspensen;
    }

    set betreuungspensen(value: Array<TSBetreuungsmitteilungPensum>) {
        this._betreuungspensen = value;
    }
}
