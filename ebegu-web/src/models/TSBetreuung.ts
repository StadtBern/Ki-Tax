import TSAbstractEntity from './TSAbstractEntity';
import TSInstitutionStammdaten from './TSInstitutionStammdaten';
import {TSBetreuungsstatus} from './enums/TSBetreuungsstatus';
import TSBetreuungspensumContainer from './TSBetreuungspensumContainer';
import TSVerfuegung from './TSVerfuegung';

export default class TSBetreuung extends TSAbstractEntity {

    private _institutionStammdaten: TSInstitutionStammdaten;
    private _betreuungsstatus: TSBetreuungsstatus;
    private _betreuungspensumContainers: Array<TSBetreuungspensumContainer>;
    private _bemerkungen: string;
    private _grundAblehnung: string;
    private _betreuungNummer: number;
    private _verfuegung: TSVerfuegung;
    private _vertrag: boolean;
    private _erweiterteBeduerfnisse: boolean;
    private _datumAblehnung: moment.Moment;
    private _datumBestaetigung: moment.Moment;


    constructor(institutionStammdaten?: TSInstitutionStammdaten, betreuungsstatus?: TSBetreuungsstatus,
                betreuungspensumContainers?: Array<TSBetreuungspensumContainer>, bemerkungen?: string,
                betreuungNummer?: number, verfuegung?: TSVerfuegung, vertrag?: boolean, erweiterteBeduerfnisse?: boolean,
                grundAblehnung?: string, datumAblehnung?: moment.Moment, datumBestaetigung?: moment.Moment) {
        super();
        this._institutionStammdaten = institutionStammdaten;
        this._betreuungsstatus = betreuungsstatus ? betreuungsstatus : TSBetreuungsstatus.AUSSTEHEND;
        this._betreuungspensumContainers = betreuungspensumContainers ? betreuungspensumContainers : [];
        this._bemerkungen = bemerkungen;
        this._grundAblehnung = grundAblehnung;
        this._betreuungNummer = betreuungNummer;
        this._verfuegung = verfuegung;
        this._vertrag = vertrag ? true : false;
        this._erweiterteBeduerfnisse = erweiterteBeduerfnisse ? true : false;
        this._datumAblehnung = datumAblehnung;
        this._datumBestaetigung = datumBestaetigung;
    }

    get institutionStammdaten(): TSInstitutionStammdaten {
        return this._institutionStammdaten;
    }

    set institutionStammdaten(value: TSInstitutionStammdaten) {
        this._institutionStammdaten = value;
    }

    get betreuungsstatus(): TSBetreuungsstatus {
        return this._betreuungsstatus;
    }

    set betreuungsstatus(value: TSBetreuungsstatus) {
        this._betreuungsstatus = value;
    }

    get betreuungspensumContainers(): Array<TSBetreuungspensumContainer> {
        return this._betreuungspensumContainers;
    }

    set betreuungspensumContainers(value: Array<TSBetreuungspensumContainer>) {
        this._betreuungspensumContainers = value;
    }

    get bemerkungen(): string {
        return this._bemerkungen;
    }

    set bemerkungen(value: string) {
        this._bemerkungen = value;
    }

    get grundAblehnung(): string {
        return this._grundAblehnung;
    }

    set grundAblehnung(value: string) {
        this._grundAblehnung = value;
    }

    get betreuungNummer(): number {
        return this._betreuungNummer;
    }

    set betreuungNummer(value: number) {
        this._betreuungNummer = value;
    }

    get verfuegung(): TSVerfuegung {
        return this._verfuegung;
    }

    set verfuegung(value: TSVerfuegung) {
        this._verfuegung = value;
    }

    get vertrag(): boolean {
        return this._vertrag;
    }

    set vertrag(value: boolean) {
        this._vertrag = value;
    }

    get erweiterteBeduerfnisse(): boolean {
        return this._erweiterteBeduerfnisse;
    }

    set erweiterteBeduerfnisse(value: boolean) {
        this._erweiterteBeduerfnisse = value;
    }

    get datumAblehnung(): moment.Moment {
        return this._datumAblehnung;
    }

    set datumAblehnung(value: moment.Moment) {
        this._datumAblehnung = value;
    }

    get datumBestaetigung(): moment.Moment {
        return this._datumBestaetigung;
    }

    set datumBestaetigung(value: moment.Moment) {
        this._datumBestaetigung = value;
    }
}
