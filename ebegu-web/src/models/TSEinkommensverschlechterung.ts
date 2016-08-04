import TSAbstractFinanzielleSituation from './TSAbstractFinanzielleSituation';

export default class TSEinkommensverschlechterung extends TSAbstractFinanzielleSituation {

    private _nettolohnJan: number;
    private _nettolohnFeb: number;
    private _nettolohnMrz: number;
    private _nettolohnApr: number;
    private _nettolohnMai: number;
    private _nettolohnJun: number;
    private _nettolohnJul: number;
    private _nettolohnAug: number;
    private _nettolohnSep: number;
    private _nettolohnOkt: number;
    private _nettolohnNov: number;
    private _nettolohnDez: number;
    private _nettolohnZus: number;

    constructor(steuerveranlagungErhalten?: boolean, steuererklaerungAusgefuellt?: boolean,
                nettolohnJan?: number, nettolohnFeb?: number, nettolohnMrz?: number,
                nettolohnApr?: number, nettolohnMai?: number, nettolohnJun?: number,
                nettolohnJul?: number, nettolohnAug?: number, nettolohnSep?: number,
                nettolohnOkt?: number, nettolohnNov?: number, nettolohnDez?: number,
                nettolohnZus?: number,
                familienzulage?: number, ersatzeinkommen?: number, erhalteneAlimente?: number, bruttovermoegen?: number,
                schulden?: number, geschaeftsgewinnBasisjahr?: number, geleisteteAlimente?: number) {
        super(steuerveranlagungErhalten, steuererklaerungAusgefuellt,
            familienzulage, ersatzeinkommen, erhalteneAlimente, bruttovermoegen,
            schulden, geschaeftsgewinnBasisjahr, geleisteteAlimente);

        this._nettolohnJan = nettolohnJan;
        this._nettolohnFeb = nettolohnFeb;
        this._nettolohnMrz = nettolohnMrz;
        this._nettolohnApr = nettolohnApr;
        this._nettolohnMai = nettolohnMai;
        this._nettolohnJun = nettolohnJun;
        this._nettolohnJul = nettolohnJul;
        this._nettolohnAug = nettolohnAug;
        this._nettolohnSep = nettolohnSep;
        this._nettolohnOkt = nettolohnOkt;
        this._nettolohnNov = nettolohnNov;
        this._nettolohnDez = nettolohnDez;
        this._nettolohnZus = nettolohnZus;
    }


    get nettolohnJan(): number {
        return this._nettolohnJan;
    }

    set nettolohnJan(value: number) {
        this._nettolohnJan = value;
    }

    get nettolohnFeb(): number {
        return this._nettolohnFeb;
    }

    set nettolohnFeb(value: number) {
        this._nettolohnFeb = value;
    }

    get nettolohnMrz(): number {
        return this._nettolohnMrz;
    }

    set nettolohnMrz(value: number) {
        this._nettolohnMrz = value;
    }

    get nettolohnApr(): number {
        return this._nettolohnApr;
    }

    set nettolohnApr(value: number) {
        this._nettolohnApr = value;
    }

    get nettolohnMai(): number {
        return this._nettolohnMai;
    }

    set nettolohnMai(value: number) {
        this._nettolohnMai = value;
    }

    get nettolohnJun(): number {
        return this._nettolohnJun;
    }

    set nettolohnJun(value: number) {
        this._nettolohnJun = value;
    }

    get nettolohnJul(): number {
        return this._nettolohnJul;
    }

    set nettolohnJul(value: number) {
        this._nettolohnJul = value;
    }

    get nettolohnAug(): number {
        return this._nettolohnAug;
    }

    set nettolohnAug(value: number) {
        this._nettolohnAug = value;
    }

    get nettolohnSep(): number {
        return this._nettolohnSep;
    }

    set nettolohnSep(value: number) {
        this._nettolohnSep = value;
    }

    get nettolohnOkt(): number {
        return this._nettolohnOkt;
    }

    set nettolohnOkt(value: number) {
        this._nettolohnOkt = value;
    }

    get nettolohnNov(): number {
        return this._nettolohnNov;
    }

    set nettolohnNov(value: number) {
        this._nettolohnNov = value;
    }

    get nettolohnDez(): number {
        return this._nettolohnDez;
    }

    set nettolohnDez(value: number) {
        this._nettolohnDez = value;
    }


    get nettolohnZus(): number {
        return this._nettolohnZus;
    }

    set nettolohnZus(value: number) {
        this._nettolohnZus = value;
    }
}
