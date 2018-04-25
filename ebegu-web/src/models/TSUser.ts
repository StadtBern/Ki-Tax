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

import {TSAmt} from './enums/TSAmt';
import {rolePrefix, TSRole} from './enums/TSRole';
import TSBerechtigung from './TSBerechtigung';
import TSInstitution from './TSInstitution';
import {TSMandant} from './TSMandant';
import {TSTraegerschaft} from './TSTraegerschaft';

export default class TSUser {

    private _nachname: string;
    private _vorname: string;
    private _username: string;
    private _password: string;
    private _email: string;
    private _mandant: TSMandant;
    private _role: TSRole;
    private _amt: TSAmt;
    private _gesperrt: boolean;

    private _currentBerechtigung: TSBerechtigung;

    //TODO (hefr) hier muss wohl doch direkt die currentBerechtigung uebergeben werden??? wegen DatumBis?
    constructor(vorname?: string, nachname?: string, username?: string, password?: string, email?: string,
                mandant?: TSMandant, role?: TSRole, traegerschaft?: TSTraegerschaft, institution?: TSInstitution,
                amt?: TSAmt, gesperrt?: boolean) {
        this._vorname = vorname;
        this._nachname = nachname;
        this._username = username;
        this._password = password;
        this._email = email;
        this._mandant = mandant;
        this._role = role;
        this._amt = amt;
        this._gesperrt = gesperrt;
        // Berechtigung
        this._currentBerechtigung = new TSBerechtigung();
        this._currentBerechtigung.role = role;
        this._currentBerechtigung.institution = institution;
        this._currentBerechtigung.traegerschaft = traegerschaft;
    }

    get nachname(): string {
        return this._nachname;
    }

    set nachname(value: string) {
        this._nachname = value;
    }

    get vorname(): string {
        return this._vorname;
    }

    set vorname(value: string) {
        this._vorname = value;
    }

    get username(): string {
        return this._username;
    }

    set username(value: string) {
        this._username = value;
    }

    get password(): string {
        return this._password;
    }

    set password(value: string) {
        this._password = value;
    }

    get email(): string {
        return this._email;
    }

    set email(value: string) {
        this._email = value;
    }

    get mandant(): TSMandant {
        return this._mandant;
    }

    set mandant(value: TSMandant) {
        this._mandant = value;
    }

    get role(): TSRole {
        return this._role;
    }

    set role(value: TSRole) {
        this._role = value;
    }

    get amt(): TSAmt {
        if (!this._amt) {
            this._amt = this.analyseAmt();
        }
        return this._amt;
    }

    set amt(value: TSAmt) {
        this._amt = value;
    }

    get gesperrt(): boolean {
        return this._gesperrt;
    }

    set gesperrt(value: boolean) {
        this._gesperrt = value;
    }

    get currentBerechtigung(): TSBerechtigung {
        return this._currentBerechtigung;
    }

    set currentBerechtigung(value: TSBerechtigung) {
        this._currentBerechtigung = value;
    }

    getFullName(): string {
        return (this.vorname ? this.vorname : '') + ' ' + (this.nachname ? this.nachname : '');
    }

    getRoleKey(): string {
        return rolePrefix() + this.role;
    }

    /**
     * Diese Methode wird im Client gebraucht, weil das Amt in der Cookie nicht gespeichert wird. Das Amt in der Cookie zu speichern
     * waere auch keine gute Loesung, da es da nicht hingehoert. Normalerweise wird das Amt aber im Server gesetzt und zum Client geschickt.
     * Diese Methode wird nur verwendet, wenn der User aus der Cookie geholt wird.
     * ACHTUNG Diese Logik existiert auch im Server UserRole. Aenderungen muessen in beiden Orten gemacht werden.
     */
    private analyseAmt(): TSAmt {
        switch (this.role) {
            case TSRole.SACHBEARBEITER_JA:
            case TSRole.ADMIN:
            case TSRole.SUPER_ADMIN:
                return TSAmt.JUGENDAMT;
            case TSRole.SCHULAMT:
            case TSRole.ADMINISTRATOR_SCHULAMT:
                return TSAmt.SCHULAMT;
            default:
                return TSAmt.NONE;
        }
    }
}
