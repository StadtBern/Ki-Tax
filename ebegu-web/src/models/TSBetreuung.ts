/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import * as moment from 'moment';
import {TSBetreuungsangebotTyp} from './enums/TSBetreuungsangebotTyp';
import {isBetreuungsstatusTSAusgeloest, TSBetreuungsstatus} from './enums/TSBetreuungsstatus';
import TSAbstractEntity from './TSAbstractEntity';
import TSAbwesenheitContainer from './TSAbwesenheitContainer';
import TSBelegungFerieninsel from './TSBelegungFerieninsel';
import TSBelegungTagesschule from './TSBelegungTagesschule';
import TSBetreuungspensumContainer from './TSBetreuungspensumContainer';
import TSGesuchsperiode from './TSGesuchsperiode';
import TSInstitutionStammdaten from './TSInstitutionStammdaten';
import TSVerfuegung from './TSVerfuegung';
import {TSAnmeldungMutationZustand} from './enums/TSAnmeldungMutationZustand';

export default class TSBetreuung extends TSAbstractEntity {

    private _institutionStammdaten: TSInstitutionStammdaten;
    private _betreuungsstatus: TSBetreuungsstatus;
    private _betreuungspensumContainers: Array<TSBetreuungspensumContainer>;
    private _abwesenheitContainers: Array<TSAbwesenheitContainer>;
    private _grundAblehnung: string;
    private _betreuungNummer: number;
    private _verfuegung: TSVerfuegung;
    private _vertrag: boolean;
    private _erweiterteBeduerfnisse: boolean;
    private _datumAblehnung: moment.Moment;
    private _datumBestaetigung: moment.Moment;
    private _kindFullname: string;
    private _kindNummer: number;
    private _gesuchId: string;
    private _gesuchsperiode: TSGesuchsperiode;
    private _betreuungMutiert: boolean;
    private _abwesenheitMutiert: boolean;
    private _gueltig: boolean;
    private _belegungTagesschule: TSBelegungTagesschule;
    private _belegungFerieninsel: TSBelegungFerieninsel;
    private _anmeldungMutationZustand: TSAnmeldungMutationZustand;


    constructor(institutionStammdaten?: TSInstitutionStammdaten, betreuungsstatus?: TSBetreuungsstatus,
                betreuungspensumContainers?: Array<TSBetreuungspensumContainer>, abwesenheitContainers?: Array<TSAbwesenheitContainer>,
                betreuungNummer?: number, verfuegung?: TSVerfuegung, vertrag?: boolean, erweiterteBeduerfnisse?: boolean,
                grundAblehnung?: string, datumAblehnung?: moment.Moment, datumBestaetigung?: moment.Moment, kindFullname?: string,
                kindNummer?: number, gesuchId?: string, gesuchsperiode?: TSGesuchsperiode,
                betreuungMutiert?: boolean, abwesenheitMutiert?: boolean, gueltig?: boolean, belegungTagesschule?: TSBelegungTagesschule,
                belegungFerieninsel?: TSBelegungFerieninsel, anmeldungMutationZustand?: TSAnmeldungMutationZustand) {
        super();
        this._institutionStammdaten = institutionStammdaten;
        this._betreuungsstatus = betreuungsstatus ? betreuungsstatus : TSBetreuungsstatus.AUSSTEHEND;
        this._betreuungspensumContainers = betreuungspensumContainers ? betreuungspensumContainers : [];
        this._abwesenheitContainers = abwesenheitContainers ? abwesenheitContainers : [];
        this._grundAblehnung = grundAblehnung;
        this._betreuungNummer = betreuungNummer;
        this._verfuegung = verfuegung;
        this._vertrag = vertrag ? true : false;
        this._erweiterteBeduerfnisse = erweiterteBeduerfnisse ? true : false;
        this._datumAblehnung = datumAblehnung;
        this._datumBestaetigung = datumBestaetigung;
        this._kindFullname = kindFullname;
        this._kindNummer = kindNummer;
        this._gesuchId = gesuchId;
        this._gesuchsperiode = gesuchsperiode;
        this._betreuungMutiert = betreuungMutiert;
        this._abwesenheitMutiert = abwesenheitMutiert;
        this._gueltig = gueltig;
        this._belegungTagesschule = belegungTagesschule;
        this._belegungFerieninsel = belegungFerieninsel;
        this._anmeldungMutationZustand = anmeldungMutationZustand;
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

    get abwesenheitContainers(): Array<TSAbwesenheitContainer> {
        return this._abwesenheitContainers;
    }

    set abwesenheitContainers(value: Array<TSAbwesenheitContainer>) {
        this._abwesenheitContainers = value;
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

    get kindFullname(): string {
        return this._kindFullname;
    }

    set kindFullname(value: string) {
        this._kindFullname = value;
    }

    get kindNummer(): number {
        return this._kindNummer;
    }

    set kindNummer(value: number) {
        this._kindNummer = value;
    }

    get gesuchId(): string {
        return this._gesuchId;
    }

    set gesuchId(value: string) {
        this._gesuchId = value;
    }

    get gesuchsperiode(): TSGesuchsperiode {
        return this._gesuchsperiode;
    }

    set gesuchsperiode(value: TSGesuchsperiode) {
        this._gesuchsperiode = value;
    }

    get betreuungMutiert(): boolean {
        return this._betreuungMutiert;
    }

    set betreuungMutiert(value: boolean) {
        this._betreuungMutiert = value;
    }

    get abwesenheitMutiert(): boolean {
        return this._abwesenheitMutiert;
    }

    set abwesenheitMutiert(value: boolean) {
        this._abwesenheitMutiert = value;
    }

    get gueltig(): boolean {
        return this._gueltig;
    }

    set gueltig(value: boolean) {
        this._gueltig = value;
    }

    public get belegungTagesschule(): TSBelegungTagesschule {
        return this._belegungTagesschule;
    }

    public set belegungTagesschule(value: TSBelegungTagesschule) {
        this._belegungTagesschule = value;
    }

    public get belegungFerieninsel(): TSBelegungFerieninsel {
        return this._belegungFerieninsel;
    }

    public set belegungFerieninsel(value: TSBelegungFerieninsel) {
        this._belegungFerieninsel = value;
    }

    public isAngebotKITA(): boolean {
       return this.isAngebot(TSBetreuungsangebotTyp.KITA);
    }

    public isAngebotTagesschule(): boolean {
       return this.isAngebot(TSBetreuungsangebotTyp.TAGESSCHULE);
    }

    public isAngebotFerieninsel(): boolean {
        return this.isAngebot(TSBetreuungsangebotTyp.FERIENINSEL);
    }

    public isAngebotSchulamt(): boolean {
        return this.isAngebotFerieninsel() || this.isAngebotTagesschule();
    }

    private isAngebot(typ: TSBetreuungsangebotTyp) {
        if (this.institutionStammdaten && this.institutionStammdaten.betreuungsangebotTyp) {
            return this.institutionStammdaten.betreuungsangebotTyp === typ;
        }
        return false;
    }

    public isEnabled(): boolean {
        return (!this.hasVorgaenger() || this.isAngebotSchulamt())
            && (this.isBetreuungsstatus(TSBetreuungsstatus.AUSSTEHEND)
            || this.isBetreuungsstatus(TSBetreuungsstatus.SCHULAMT_ANMELDUNG_ERFASST));
    }

    public isBetreuungsstatus(status: TSBetreuungsstatus): boolean {
        return this.betreuungsstatus === status;
    }

    public isSchulamtangebotAusgeloest(): boolean {
        return this.isAngebotSchulamt() && isBetreuungsstatusTSAusgeloest(this.betreuungsstatus);
    }

    public get anmeldungMutationZustand(): TSAnmeldungMutationZustand {
        return this._anmeldungMutationZustand;
    }

    public set anmeldungMutationZustand(value: TSAnmeldungMutationZustand) {
        this._anmeldungMutationZustand = value;
    }
}
