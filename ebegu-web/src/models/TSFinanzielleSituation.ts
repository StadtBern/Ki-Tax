import TSAbstractFinanzielleSituation from './TSAbstractFinanzielleSituation';

export default class TSFinanzielleSituation extends TSAbstractFinanzielleSituation {


    private _nettolohn: number;

    constructor(steuerveranlagungErhalten?: boolean, steuererklaerungAusgefuellt?: boolean, nettolohn?: number,
                familienzulage?: number, ersatzeinkommen?: number, erhalteneAlimente?: number, bruttovermoegen?: number,
                schulden?: number, geschaeftsgewinnBasisjahrMinus2?: number,
                geschaeftsgewinnBasisjahrMinus1?: number, geschaeftsgewinnBasisjahr?: number, geleisteteAlimente?: number) {
        super(steuerveranlagungErhalten, steuererklaerungAusgefuellt,
            familienzulage, ersatzeinkommen, erhalteneAlimente, bruttovermoegen,
            schulden, geschaeftsgewinnBasisjahrMinus2,
            geschaeftsgewinnBasisjahrMinus1, geschaeftsgewinnBasisjahr, geleisteteAlimente);
        this._nettolohn = nettolohn;
    }


    get nettolohn(): number {
        return this._nettolohn;
    }

    set nettolohn(value: number) {
        this._nettolohn = value;
    }
}
