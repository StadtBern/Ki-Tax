import TSAdresse from './TSAdresse';
import TSAbstractPersonEntity from './TSAbstractPersonEntity';
import {TSGeschlecht} from './enums/TSGeschlecht';
import TSFinanzielleSituationContainer from './TSFinanzielleSituationContainer';
import TSErwerbspensumContainer from './TSErwerbspensumContainer';
import TSEinkommensverschlechterungContainer from './TSEinkommensverschlechterungContainer';

export default class TSGesuchsteller extends TSAbstractPersonEntity {

    private _mail: string;
    private _mobile: string;
    private _telefon: string;
    private _telefonAusland: string;
    private _adressen: Array<TSAdresse>;
    private _korrespondenzAdresse: TSAdresse;
    private _finanzielleSituationContainer: TSFinanzielleSituationContainer;
    private _erwerbspensenContainer: Array<TSErwerbspensumContainer>;
    private _diplomatenstatus: boolean;
    private _einkommensverschlechterungContainer: TSEinkommensverschlechterungContainer;

    constructor(vorname?: string, nachname?: string, geburtsdatum?: moment.Moment, geschlecht?: TSGeschlecht,
                email?: string, mobile?: string, telefon?: string, telefonAusland?: string,
                finanzielleSituation?: TSFinanzielleSituationContainer, erwerbspensen?: Array<TSErwerbspensumContainer>,
                diplomatenstatus?: boolean, einkommensverschlechterungContainer?: TSEinkommensverschlechterungContainer) {
        super(vorname, nachname, geburtsdatum, geschlecht);
        this._mail = email;
        this._mobile = mobile;
        this._telefon = telefon;
        this._telefonAusland = telefonAusland;
        this._finanzielleSituationContainer = finanzielleSituation;
        this._erwerbspensenContainer = erwerbspensen ? erwerbspensen : [];
        this._diplomatenstatus = diplomatenstatus;
        this._einkommensverschlechterungContainer = einkommensverschlechterungContainer;
    }

    public get mail(): string {
        return this._mail;
    }

    public set mail(value: string) {
        this._mail = value;
    }

    public get mobile(): string {
        return this._mobile;
    }

    public set mobile(value: string) {
        this._mobile = value;
    }

    public get telefon(): string {
        return this._telefon;
    }

    public set telefon(value: string) {
        this._telefon = value;
    }

    public get adressen(): Array<TSAdresse> {
        return this._adressen;
    }

    public set adressen(adr: Array<TSAdresse>) {
        this._adressen = adr;
    }

    public addAdresse(value: TSAdresse) {
        if (this._adressen) {
            this._adressen.push(value);
        }
    }

    public get telefonAusland(): string {
        return this._telefonAusland;
    }

    public set telefonAusland(value: string) {
        this._telefonAusland = value;
    }

    public get korrespondenzAdresse(): TSAdresse {
        return this._korrespondenzAdresse;
    }

    public set korrespondenzAdresse(value: TSAdresse) {
        this._korrespondenzAdresse = value;
    }

    public get finanzielleSituationContainer(): TSFinanzielleSituationContainer {
        return this._finanzielleSituationContainer;
    }

    public set finanzielleSituationContainer(value: TSFinanzielleSituationContainer) {
        this._finanzielleSituationContainer = value;
    }

    get erwerbspensenContainer(): Array<TSErwerbspensumContainer> {
        return this._erwerbspensenContainer;
    }

    set erwerbspensenContainer(value: Array<TSErwerbspensumContainer>) {
        this._erwerbspensenContainer = value;
    }

    get diplomatenstatus(): boolean {
        return this._diplomatenstatus;
    }

    set diplomatenstatus(value: boolean) {
        this._diplomatenstatus = value;
    }

    get einkommensverschlechterungContainer(): TSEinkommensverschlechterungContainer {
        return this._einkommensverschlechterungContainer;
    }

    set einkommensverschlechterungContainer(value: TSEinkommensverschlechterungContainer) {
        this._einkommensverschlechterungContainer = value;
    }

    public getPhone(): string {
        if (this.mobile) {
            return this.mobile;
        } else if (this.telefon) {
            return this.telefon;
        } else {
            return '';
        }
    }

    public getFirstAdresse(): TSAdresse {
        return this.adressen[0];
    }

}

