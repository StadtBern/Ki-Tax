import TSAdresse from './TSAdresse';
import TSAbstractPersonEntity from './TSAbstractPersonEntity';
import {TSGeschlecht} from './enums/TSGeschlecht';
import TSFinanzielleSituationContainer from './TSFinanzielleSituationContainer';
import TSErwerbspensumContainer from './TSErwerbspensumContainer';

export default class TSGesuchsteller extends TSAbstractPersonEntity {

    private _mail: string;
    private _mobile: string;
    private _telefon: string;
    private _telefonAusland: string;
    private _umzug: boolean;
    private _adresse: TSAdresse;
    private _korrespondenzAdresse: TSAdresse;
    private _umzugAdresse: TSAdresse;
    private _finanzielleSituationContainer: TSFinanzielleSituationContainer;
    private _erwerbspensenContainer: Array<TSErwerbspensumContainer>;

    constructor(vorname?: string, nachname?: string, geburtsdatum?: moment.Moment, geschlecht?: TSGeschlecht,
                email?: string, mobile?: string, telefon?: string, telefonAusland?: string, umzug?: boolean,
                finanzielleSituation?: TSFinanzielleSituationContainer, erwerbspensen?: Array<TSErwerbspensumContainer>) {
        super(vorname, nachname, geburtsdatum, geschlecht);
        this._mail = email;
        this._mobile = mobile;
        this._telefon = telefon;
        this._telefonAusland = telefonAusland;
        this._umzug = umzug;
        this._finanzielleSituationContainer = finanzielleSituation;
        this._erwerbspensenContainer = erwerbspensen ? erwerbspensen : [];
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

    public get umzug(): boolean {
        return this._umzug;
    }

    public set umzug(value: boolean) {
        this._umzug = value;
    }

    public get adresse(): TSAdresse {
        return this._adresse;
    }

    public set adresse(adr: TSAdresse) {
        this._adresse = adr;
    }

    public get umzugAdresse(): TSAdresse {
        return this._umzugAdresse;
    }

    public set umzugAdresse(value: TSAdresse) {
        this._umzugAdresse = value;
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
}
