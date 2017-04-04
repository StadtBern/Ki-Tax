import TSAbstractEntity from './TSAbstractEntity';
import * as moment from 'moment';
/**
 * DTO für eine Adresse aus dem EWK
 */
export default class TSEWKAdresse extends TSAbstractEntity {


    private _adresstyp: string;
    private _adresstypTxt: string;
    private _gueltigVon: moment.Moment;
    private _gueltigBis: moment.Moment;
    private _coName: string;
    private _postfach: string;
    private _bfSGemeinde: string;
    private _strasse: string;
    private _hausnummer: string;
    private _postleitzahl: string;
    private _ort: string;
    private _kanton: string;
    private _land: string;


    constructor(adresstyp?: string, adresstypTxt?: string, gueltigVon?: moment.Moment, gueltigBis?: moment.Moment,
                coName?: string, postfach?: string, bfSGemeinde?: string, strasse?: string, hausnummer?: string,
                postleitzahl?: string, ort?: string, kanton?: string, land?: string) {
        super();
        this._adresstyp = adresstyp;
        this._adresstypTxt = adresstypTxt;
        this._gueltigVon = gueltigVon;
        this._gueltigBis = gueltigBis;
        this._coName = coName;
        this._postfach = postfach;
        this._bfSGemeinde = bfSGemeinde;
        this._strasse = strasse;
        this._hausnummer = hausnummer;
        this._postleitzahl = postleitzahl;
        this._ort = ort;
        this._kanton = kanton;
        this._land = land;
    }


    get adresstyp(): string {
        return this._adresstyp;
    }

    set adresstyp(value: string) {
        this._adresstyp = value;
    }

    get adresstypTxt(): string {
        return this._adresstypTxt;
    }

    set adresstypTxt(value: string) {
        this._adresstypTxt = value;
    }

    get gueltigVon(): moment.Moment {
        return this._gueltigVon;
    }

    set gueltigVon(value: moment.Moment) {
        this._gueltigVon = value;
    }

    get gueltigBis(): moment.Moment {
        return this._gueltigBis;
    }

    set gueltigBis(value: moment.Moment) {
        this._gueltigBis = value;
    }

    get coName(): string {
        return this._coName;
    }

    set coName(value: string) {
        this._coName = value;
    }

    get postfach(): string {
        return this._postfach;
    }

    set postfach(value: string) {
        this._postfach = value;
    }

    get bfSGemeinde(): string {
        return this._bfSGemeinde;
    }

    set bfSGemeinde(value: string) {
        this._bfSGemeinde = value;
    }

    get strasse(): string {
        return this._strasse;
    }

    set strasse(value: string) {
        this._strasse = value;
    }

    get hausnummer(): string {
        return this._hausnummer;
    }

    set hausnummer(value: string) {
        this._hausnummer = value;
    }

    get postleitzahl(): string {
        return this._postleitzahl;
    }

    set postleitzahl(value: string) {
        this._postleitzahl = value;
    }

    get ort(): string {
        return this._ort;
    }

    set ort(value: string) {
        this._ort = value;
    }

    get kanton(): string {
        return this._kanton;
    }

    set kanton(value: string) {
        this._kanton = value;
    }

    get land(): string {
        return this._land;
    }

    set land(value: string) {
        this._land = value;
    }

    public getShortDescription(): string {
        return this.strasse + ' ' + this.hausnummer + ', ' + this.postleitzahl + ' ' + this.ort;
    }
}
