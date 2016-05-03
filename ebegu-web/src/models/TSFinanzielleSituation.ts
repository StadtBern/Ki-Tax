import TSAbstractEntity from './TSAbstractEntity';

export default class TSFinanzielleSituation extends TSAbstractEntity {

    private _steuerveranlagungErhalten: boolean;
    private _steuererklaerungAusgefuellt: boolean = false;
    private _nettolohn: number;
    private _familienzulage: number;
    private _ersatzeinkommen: number;
    private _erhalteneAlimente: number;
    private _bruttovermoegen: number;
    private _schulden: number;
    private _selbstaendig: boolean;
    private _geschaeftsgewinnBasisjahrMinus2: number;
    private _geschaeftsgewinnBasisjahrMinus1: number;
    private _geschaeftsgewinnBasisjahr: number;
    private _geleisteteAlimente: number;


    constructor(steuerveranlagungErhalten?: boolean, steuererklaerungAusgefuellt?: boolean, nettolohn?: number,
                familienzulage?: number, ersatzeinkommen?: number, erhalteneAlimente?: number, bruttovermoegen?: number,
                schulden?: number, selbstaendig?: boolean, geschaeftsgewinnBasisjahrMinus2?: number,
                geschaeftsgewinnBasisjahrMinus1?: number, geschaeftsgewinnBasisjahr?: number, geleisteteAlimente?: number) {
        super();
        this._steuerveranlagungErhalten = steuerveranlagungErhalten;
        this._steuererklaerungAusgefuellt = steuererklaerungAusgefuellt;
        this._nettolohn = nettolohn;
        this._familienzulage = familienzulage;
        this._ersatzeinkommen = ersatzeinkommen;
        this._erhalteneAlimente = erhalteneAlimente;
        this._bruttovermoegen = bruttovermoegen;
        this._schulden = schulden;
        this._selbstaendig = selbstaendig;
        this._geschaeftsgewinnBasisjahrMinus2 = geschaeftsgewinnBasisjahrMinus2;
        this._geschaeftsgewinnBasisjahrMinus1 = geschaeftsgewinnBasisjahrMinus1;
        this._geschaeftsgewinnBasisjahr = geschaeftsgewinnBasisjahr;
        this._geleisteteAlimente = geleisteteAlimente;
    }

    get steuerveranlagungErhalten(): boolean {
        return this._steuerveranlagungErhalten;
    }

    set steuerveranlagungErhalten(value: boolean) {
        this._steuerveranlagungErhalten = value;
    }

    get steuererklaerungAusgefuellt(): boolean {
        return this._steuererklaerungAusgefuellt;
    }

    set steuererklaerungAusgefuellt(value: boolean) {
        this._steuererklaerungAusgefuellt = value;
    }

    get nettolohn(): number {
        return this._nettolohn;
    }

    set nettolohn(value: number) {
        this._nettolohn = value;
    }

    get familienzulage(): number {
        return this._familienzulage;
    }

    set familienzulage(value: number) {
        this._familienzulage = value;
    }

    get ersatzeinkommen(): number {
        return this._ersatzeinkommen;
    }

    set ersatzeinkommen(value: number) {
        this._ersatzeinkommen = value;
    }

    get erhalteneAlimente(): number {
        return this._erhalteneAlimente;
    }

    set erhalteneAlimente(value: number) {
        this._erhalteneAlimente = value;
    }

    get bruttovermoegen(): number {
        return this._bruttovermoegen;
    }

    set bruttovermoegen(value: number) {
        this._bruttovermoegen = value;
    }

    get schulden(): number {
        return this._schulden;
    }

    set schulden(value: number) {
        this._schulden = value;
    }

    get selbstaendig(): boolean {
        return this._selbstaendig;
    }

    set selbstaendig(value: boolean) {
        this._selbstaendig = value;
    }

    get geschaeftsgewinnBasisjahrMinus2(): number {
        return this._geschaeftsgewinnBasisjahrMinus2;
    }

    set geschaeftsgewinnBasisjahrMinus2(value: number) {
        this._geschaeftsgewinnBasisjahrMinus2 = value;
    }

    get geschaeftsgewinnBasisjahrMinus1(): number {
        return this._geschaeftsgewinnBasisjahrMinus1;
    }

    set geschaeftsgewinnBasisjahrMinus1(value: number) {
        this._geschaeftsgewinnBasisjahrMinus1 = value;
    }

    get geschaeftsgewinnBasisjahr(): number {
        return this._geschaeftsgewinnBasisjahr;
    }

    set geschaeftsgewinnBasisjahr(value: number) {
        this._geschaeftsgewinnBasisjahr = value;
    }

    get geleisteteAlimente(): number {
        return this._geleisteteAlimente;
    }

    set geleisteteAlimente(value: number) {
        this._geleisteteAlimente = value;
    }
}
