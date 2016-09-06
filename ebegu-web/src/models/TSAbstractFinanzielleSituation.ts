import TSAbstractEntity from './TSAbstractEntity';

export default class TSAbstractFinanzielleSituation extends TSAbstractEntity {

    private _steuerveranlagungErhalten: boolean;
    private _steuererklaerungAusgefuellt: boolean;
    private _familienzulage: number;
    private _ersatzeinkommen: number;
    private _erhalteneAlimente: number;
    private _bruttovermoegen: number;
    private _schulden: number;
    private _geschaeftsgewinnBasisjahr: number;
    private _geleisteteAlimente: number;


    constructor(steuerveranlagungErhalten?: boolean, steuererklaerungAusgefuellt?: boolean,
                familienzulage?: number, ersatzeinkommen?: number, erhalteneAlimente?: number, bruttovermoegen?: number,
                schulden?: number, geschaeftsgewinnBasisjahr?: number, geleisteteAlimente?: number) {
        super();
        this._steuerveranlagungErhalten = steuerveranlagungErhalten || false;
        this._steuererklaerungAusgefuellt = steuererklaerungAusgefuellt;
        this._familienzulage = familienzulage;
        this._ersatzeinkommen = ersatzeinkommen;
        this._erhalteneAlimente = erhalteneAlimente;
        this._bruttovermoegen = bruttovermoegen;
        this._schulden = schulden;
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
