import TSAbstractPersonEntity from './TSAbstractPersonEntity';
import {TSGeschlecht} from './enums/TSGeschlecht';
import * as moment from 'moment';
import Moment = moment.Moment;

export default class TSGesuchsteller extends TSAbstractPersonEntity {

    private _mail: string;
    private _mobile: string;
    private _telefon: string;
    private _telefonAusland: string;
    private _diplomatenstatus: boolean;
    private _ewkPersonId: string;
    private _ewkAbfrageDatum: moment.Moment;

    constructor(vorname?: string, nachname?: string, geburtsdatum?: moment.Moment, geschlecht?: TSGeschlecht,
                email?: string, mobile?: string, telefon?: string, telefonAusland?: string,
                diplomatenstatus?: boolean, ewkPersonId?: string, ewkAbfrageDatum?: moment.Moment) {
        super(vorname, nachname, geburtsdatum, geschlecht);
        this._mail = email;
        this._mobile = mobile;
        this._telefon = telefon;
        this._telefonAusland = telefonAusland;
        this._diplomatenstatus = diplomatenstatus;
        this._ewkPersonId = ewkPersonId;
        this._ewkAbfrageDatum = ewkAbfrageDatum;
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

    public get telefonAusland(): string {
        return this._telefonAusland;
    }

    public set telefonAusland(value: string) {
        this._telefonAusland = value;
    }

    get diplomatenstatus(): boolean {
        return this._diplomatenstatus;
    }

    set diplomatenstatus(value: boolean) {
        this._diplomatenstatus = value;
    }

    get ewkPersonId(): string {
        return this._ewkPersonId;
    }

    set ewkPersonId(value: string) {
        this._ewkPersonId = value;
    }

    get ewkAbfrageDatum(): moment.Moment {
        return this._ewkAbfrageDatum;
    }

    set ewkAbfrageDatum(value: moment.Moment) {
        this._ewkAbfrageDatum = value;
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
}

