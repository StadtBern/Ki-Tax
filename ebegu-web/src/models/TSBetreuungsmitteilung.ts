import TSMitteilung from './TSMitteilung';
import TSBetreuungsmitteilungPensum from './TSBetreuungsmitteilungPensum';

export default class TSBetreuungsmitteilung extends TSMitteilung {

    private _betreuungspensen: Array<TSBetreuungsmitteilungPensum>;
    private _applied: boolean;


    constructor(betreuungspensen?: Array<TSBetreuungsmitteilungPensum>, applied?: boolean) {
        super();
        this._betreuungspensen = betreuungspensen;
        this._applied = applied;
    }


    get betreuungspensen(): Array<TSBetreuungsmitteilungPensum> {
        return this._betreuungspensen;
    }

    set betreuungspensen(value: Array<TSBetreuungsmitteilungPensum>) {
        this._betreuungspensen = value;
    }

    get applied(): boolean {
        return this._applied;
    }

    set applied(value: boolean) {
        this._applied = value;
    }
}
