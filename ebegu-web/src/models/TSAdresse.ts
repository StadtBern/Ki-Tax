import TSAbstractEntity from "./TSAbstractEntity";
import {TSAdressetyp} from "./enums/TSAdressetyp";

export default class TSAdresse extends TSAbstractEntity {

    private _strasse: string;
    private _hausnummer: string;
    private _zusatzzeile: string;
    private _plz: string;
    private _ort: string;
    private _land: string;
    private _gemeinde: string;
    private _showDatumVon: boolean;
    private _gueltigAb: moment.Moment;
    private _gueltigBis: moment.Moment;
    private _adresseTyp: TSAdressetyp = TSAdressetyp.WOHNADRESSE;

    constructor(strasse?: string, hausnummer?: string, zusatzzeile?: string, plz?: string, ort?: string,
                land?: string, gemeinde?: string, gueltigAb?: moment.Moment, gueltigBis?: moment.Moment, adresseTyp?: TSAdressetyp) {
        super();
        this._strasse = strasse;
        this._hausnummer = hausnummer;
        this._zusatzzeile = zusatzzeile;
        this._plz = plz;
        this._ort = ort;
        this._land = land || 'Land_CH';
        this._gemeinde = gemeinde;
        this._gueltigAb = gueltigAb;
        this._gueltigBis = gueltigBis;
        this._adresseTyp = adresseTyp;

    }


    public get strasse(): string {
        return this._strasse;
    }

    public set strasse(value: string) {
        this._strasse = value;
    }

    public get hausnummer(): string {
        return this._hausnummer;
    }

    public set hausnummer(value: string) {
        this._hausnummer = value;
    }

    public get zusatzzeile(): string {
        return this._zusatzzeile;
    }

    public set zusatzzeile(value: string) {
        this._zusatzzeile = value;
    }

    public get plz(): string {
        return this._plz;
    }

    public set plz(value: string) {
        this._plz = value;
    }

    public get ort(): string {
        return this._ort;
    }

    public set ort(value: string) {
        this._ort = value;
    }

    public get land(): any {
        return this._land;
    }

    public set land(value: any) {
        this._land = value;
    }

    public get gemeinde(): string {
        return this._gemeinde;
    }

    public set gemeinde(value: string) {
        this._gemeinde = value;
    }

    public get gueltigAb(): moment.Moment {
        return this._gueltigAb;
    }

    public set gueltigAb(value: moment.Moment) {
        this._gueltigAb = value;
    }

    public get gueltigBis(): moment.Moment {
        return this._gueltigBis;
    }

    public set gueltigBis(value: moment.Moment) {
        this._gueltigBis = value;
    }


    public get showDatumVon(): boolean {
        return this._showDatumVon;
    }

    public set showDatumVon(value: boolean) {
        this._showDatumVon = value;
    }


    get adresseTyp(): TSAdressetyp {
        return this._adresseTyp;
    }

    set adresseTyp(value: TSAdressetyp) {
        this._adresseTyp = value;
    }
}
