import TSAbstractEntity from './TSAbstractEntity';
import * as moment from 'moment';
import {TSGeschlecht} from './enums/TSGeschlecht';
import TSEWKEinwohnercode from './TSEWKEinwohnercode';
import TSEWKAdresse from './TSEWKAdresse';
import TSEWKBeziehung from './TSEWKBeziehung';
/**
 * DTO für eine Person aus dem EWK
 */
export default class TSEWKPerson extends TSAbstractEntity {

    private _personID: string;
    private _einwohnercodes: Array<TSEWKEinwohnercode>;
    private _nachname: string;
    private _ledigname: string;
    private _vorname: string;
    private _rufname: string;
    private _geburtsdatum: moment.Moment;
    private _zuzugsdatum: moment.Moment;
    private _nationalitaet: string;
    private _zivilstand: string;
    private _zivilstandTxt: string;
    private _zivilstandsdatum: moment.Moment;
    private _geschlecht: TSGeschlecht;
    private _bewilligungsart: string;
    private _bewilligungsartTxt: string;
    private _bewilligungBis: moment.Moment;
    private _adressen: Array<TSEWKAdresse>;
    private _beziehungen: Array<TSEWKBeziehung>;


    constructor(personID?: string, einwohnercodes?: Array<TSEWKEinwohnercode>, nachname?: string, ledigname?: string,
                vorname?: string, rufname?: string, geburtsdatum?: moment.Moment, zuzugsdatum?: moment.Moment,
                nationalitaet?: string, zivilstand?: string, zivilstandTxt?: string, zivilstandsdatum?: moment.Moment,
                geschlecht?: TSGeschlecht, bewilligungsart?: string, bewilligungsartTxt?: string, bewilligungBis?: moment.Moment,
                adressen?: Array<TSEWKAdresse>, beziehungen?: Array<TSEWKBeziehung>) {
        super();
        this._personID = personID;
        this._einwohnercodes = einwohnercodes;
        this._nachname = nachname;
        this._ledigname = ledigname;
        this._vorname = vorname;
        this._rufname = rufname;
        this._geburtsdatum = geburtsdatum;
        this._zuzugsdatum = zuzugsdatum;
        this._nationalitaet = nationalitaet;
        this._zivilstand = zivilstand;
        this._zivilstandTxt = zivilstandTxt;
        this._zivilstandsdatum = zivilstandsdatum;
        this._geschlecht = geschlecht;
        this._bewilligungsart = bewilligungsart;
        this._bewilligungsartTxt = bewilligungsartTxt;
        this._bewilligungBis = bewilligungBis;
        this._adressen = adressen;
        this._beziehungen = beziehungen;
    }

    get personID(): string {
        return this._personID;
    }

    set personID(value: string) {
        this._personID = value;
    }

    get einwohnercodes(): Array<TSEWKEinwohnercode> {
        return this._einwohnercodes;
    }

    set einwohnercodes(value: Array<TSEWKEinwohnercode>) {
        this._einwohnercodes = value;
    }

    get nachname(): string {
        return this._nachname;
    }

    set nachname(value: string) {
        this._nachname = value;
    }

    get ledigname(): string {
        return this._ledigname;
    }

    set ledigname(value: string) {
        this._ledigname = value;
    }

    get vorname(): string {
        return this._vorname;
    }

    set vorname(value: string) {
        this._vorname = value;
    }

    get rufname(): string {
        return this._rufname;
    }

    set rufname(value: string) {
        this._rufname = value;
    }

    get geburtsdatum(): moment.Moment {
        return this._geburtsdatum;
    }

    set geburtsdatum(value: moment.Moment) {
        this._geburtsdatum = value;
    }

    get zuzugsdatum(): moment.Moment {
        return this._zuzugsdatum;
    }

    set zuzugsdatum(value: moment.Moment) {
        this._zuzugsdatum = value;
    }

    get nationalitaet(): string {
        return this._nationalitaet;
    }

    set nationalitaet(value: string) {
        this._nationalitaet = value;
    }

    get zivilstand(): string {
        return this._zivilstand;
    }

    set zivilstand(value: string) {
        this._zivilstand = value;
    }

    get zivilstandTxt(): string {
        return this._zivilstandTxt;
    }

    set zivilstandTxt(value: string) {
        this._zivilstandTxt = value;
    }

    get zivilstandsdatum(): moment.Moment {
        return this._zivilstandsdatum;
    }

    set zivilstandsdatum(value: moment.Moment) {
        this._zivilstandsdatum = value;
    }

    get geschlecht(): TSGeschlecht {
        return this._geschlecht;
    }

    set geschlecht(value: TSGeschlecht) {
        this._geschlecht = value;
    }

    get bewilligungsart(): string {
        return this._bewilligungsart;
    }

    set bewilligungsart(value: string) {
        this._bewilligungsart = value;
    }

    get bewilligungsartTxt(): string {
        return this._bewilligungsartTxt;
    }

    set bewilligungsartTxt(value: string) {
        this._bewilligungsartTxt = value;
    }

    get bewilligungBis(): moment.Moment {
        return this._bewilligungBis;
    }

    set bewilligungBis(value: moment.Moment) {
        this._bewilligungBis = value;
    }

    get adressen(): Array<TSEWKAdresse> {
        return this._adressen;
    }

    set adressen(value: Array<TSEWKAdresse>) {
        this._adressen = value;
    }

    get beziehungen(): Array<TSEWKBeziehung> {
        return this._beziehungen;
    }

    set beziehungen(value: Array<TSEWKBeziehung>) {
        this._beziehungen = value;
    }
}
