import {TSAdressetyp} from './enums/TSAdressetyp';
import {TSDateRange} from './types/TSDateRange';
import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';

export default class TSAdresse extends TSAbstractDateRangedEntity {

    private _strasse: string;
    private _hausnummer: string;
    private _zusatzzeile: string;
    private _plz: string;
    private _ort: string;
    private _land: string;
    private _gemeinde: string;
    private _showDatumVon: boolean;
    private _adresseTyp: TSAdressetyp = TSAdressetyp.WOHNADRESSE;
    private _organisation: string;

    constructor(strasse?: string, hausnummer?: string, zusatzzeile?: string, plz?: string, ort?: string,
                land?: string, gemeinde?: string, gueltigkeit?: TSDateRange, adresseTyp?: TSAdressetyp, organisation?: string) {
        super(gueltigkeit);
        this._strasse = strasse;
        this._hausnummer = hausnummer;
        this._zusatzzeile = zusatzzeile;
        this._plz = plz;
        this._ort = ort;
        this._land = land || 'CH';
        this._gemeinde = gemeinde;
        this._adresseTyp = adresseTyp;
        this._organisation = organisation;
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

    public get land(): string {
        return this._land;
    }

    public set land(value: string) {
        this._land = value;
    }

    public get gemeinde(): string {
        return this._gemeinde;
    }

    public set gemeinde(value: string) {
        this._gemeinde = value;
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

    get organisation(): string {
        return this._organisation;
    }

    set organisation(value: string) {
        this._organisation = value;
    }
}
