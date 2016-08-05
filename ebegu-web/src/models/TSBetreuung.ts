import TSAbstractEntity from './TSAbstractEntity';
import TSInstitutionStammdaten from './TSInstitutionStammdaten';
import {TSBetreuungsstatus} from './enums/TSBetreuungsstatus';
import TSBetreuungspensumContainer from './TSBetreuungspensumContainer';
import TSVerfuegung from './TSVerfuegung';

export default class TSBetreuung extends TSAbstractEntity {

    private _institutionStammdaten: TSInstitutionStammdaten;
    private _betreuungsstatus: TSBetreuungsstatus;
    private _betreuungspensumContainers: Array<TSBetreuungspensumContainer>;
    private _schulpflichtig: boolean;
    private _bemerkungen: string;
    private _betreuungNummer: number;
    private _verfuegung: TSVerfuegung;
    private _vertrag: boolean;

    constructor(institutionStammdaten?: TSInstitutionStammdaten, betreuungsstatus?: TSBetreuungsstatus,
                betreuungspensumContainers?: Array<TSBetreuungspensumContainer>, bemerkungen?: string, schulpflichtig?: boolean,
                betreuungNummer?: number, verfuegung?: TSVerfuegung, vertrag?: boolean) {
        super();
        this._institutionStammdaten = institutionStammdaten;
        this._betreuungsstatus = betreuungsstatus ? betreuungsstatus : TSBetreuungsstatus.AUSSTEHEND;
        this._betreuungspensumContainers = betreuungspensumContainers ? betreuungspensumContainers : [];
        this._bemerkungen = bemerkungen;
        this._schulpflichtig = schulpflichtig;
        this._betreuungNummer = betreuungNummer;
        this._verfuegung = verfuegung;
        this._vertrag = vertrag ? true : false;
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

    get schulpflichtig(): boolean {
        return this._schulpflichtig;
    }

    set schulpflichtig(value: boolean) {
        this._schulpflichtig = value;
    }

    get bemerkungen(): string {
        return this._bemerkungen;
    }

    set bemerkungen(value: string) {
        this._bemerkungen = value;
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
}
