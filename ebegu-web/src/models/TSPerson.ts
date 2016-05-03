import TSAbstractEntity from './TSAbstractEntity';
import TSAdresse from './TSAdresse';
import {TSGeschlecht} from './enums/TSGeschlecht';
import TSFinanzielleSituationContainer from './TSFinanzielleSituationContainer';

export default class TSPerson extends TSAbstractEntity {

    private _vorname: string;
    private _nachname: string;
    private _geburtsdatum: moment.Moment;
    private _mail: string;
    private _mobile: string;
    private _telefon: string;
    private _telefonAusland: string;
    private _umzug: boolean;
    private _geschlecht: TSGeschlecht;
    private _adresse: TSAdresse;
    private _korrespondenzAdresse: TSAdresse;
    private _umzugAdresse: TSAdresse;
    private _finanzielleSituationContainer: TSFinanzielleSituationContainer;

    constructor(vorname?: string, nachname?: string, geburtsdatum?: moment.Moment, email?: string, mobile?: string,
                telefon?: string, telefonAusland?: string, umzug?: boolean) {
        super();
        this._vorname = vorname;
        this._nachname = nachname;
        this._geburtsdatum = geburtsdatum;
        this._mail = email;
        this._mobile = mobile;
        this._telefon = telefon;
        this._telefonAusland = telefonAusland;
        this._umzug = umzug;
    }


    public get vorname(): string {
        return this._vorname;
    }

    public set vorname(value: string) {
        this._vorname = value;
    }

    public get nachname(): string {
        return this._nachname;
    }

    public set nachname(value: string) {
        this._nachname = value;
    }

    public get geburtsdatum(): moment.Moment {
        return this._geburtsdatum;
    }

    public set geburtsdatum(value: moment.Moment) {
        this._geburtsdatum = value;
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

    public get geschlecht(): TSGeschlecht {
        return this._geschlecht;
    }

    public set geschlecht(value: TSGeschlecht) {
        this._geschlecht = value;
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

    public getFullName(): string {
        return (this.vorname ? this.vorname :  '')  + ' ' + (this.nachname ?  this.nachname : '');
    }
}
