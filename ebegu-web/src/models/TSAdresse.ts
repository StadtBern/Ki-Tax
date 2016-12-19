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
    private _adresseTyp: TSAdressetyp = TSAdressetyp.WOHNADRESSE;
    private _nichtInGemeinde: boolean;
    private _organisation: string;

    constructor(strasse?: string, hausnummer?: string, zusatzzeile?: string, plz?: string, ort?: string,
                land?: string, gemeinde?: string, gueltigkeit?: TSDateRange, adresseTyp?: TSAdressetyp, nichtInGemeinde?: boolean, organisation?: string) {
        super(gueltigkeit);
        this._strasse = strasse;
        this._hausnummer = hausnummer;
        this._zusatzzeile = zusatzzeile;
        this._plz = plz;
        this._ort = ort;
        this._land = land || 'CH';
        this._gemeinde = gemeinde;
        this._adresseTyp = adresseTyp;
        this._nichtInGemeinde = nichtInGemeinde;
        this._organisation = organisation;
    }

    /**
     * Diese Methode sollte nur benutzt werden, um Wohnadressen zu vergleichen
     * @param other
     */
    public isSameWohnAdresse(other: TSAdresse): boolean {
        return (
            this._strasse === other.strasse &&
            this._hausnummer === other.hausnummer &&
            this._zusatzzeile === other.zusatzzeile &&
            this._plz === other.plz &&
            this._ort === other.ort &&
            this._land === other.land &&
            this._gemeinde === other.gemeinde &&
            this._adresseTyp === other.adresseTyp &&
            this._nichtInGemeinde === other.nichtInGemeinde &&
            this.gueltigkeit.gueltigAb.isSame(other.gueltigkeit.gueltigAb)
            // gueltigBis wird nicht gecheckt, da es nur relevant ist, wann sie eingezogen sind
        );
    }


    public copy(toCopy: TSAdresse): void {
        this._strasse = toCopy.strasse;
        this._hausnummer = toCopy.hausnummer;
        this._zusatzzeile = toCopy.zusatzzeile;
        this._plz = toCopy.plz;
        this._ort = toCopy.ort;
        this._land = toCopy.land;
        this._gemeinde = toCopy.gemeinde;
        this._adresseTyp = toCopy.adresseTyp;
        this._nichtInGemeinde = toCopy.nichtInGemeinde;
        this.gueltigkeit.gueltigAb = toCopy.gueltigkeit.gueltigAb;
        this.gueltigkeit.gueltigBis = toCopy.gueltigkeit.gueltigBis;
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

    get adresseTyp(): TSAdressetyp {
        return this._adresseTyp;
    }

    set adresseTyp(value: TSAdressetyp) {
        this._adresseTyp = value;
    }

    public get nichtInGemeinde(): boolean {
        return this._nichtInGemeinde;
    }

    public set nichtInGemeinde(value: boolean) {
        this._nichtInGemeinde = value;
    }

    get organisation(): string {
        return this._organisation;
    }

    set organisation(value: string) {
        this._organisation = value;
    }
}
