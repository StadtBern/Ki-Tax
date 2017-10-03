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

import {TSRole, rolePrefix} from './enums/TSRole';
import {TSMandant} from './TSMandant';
import {TSTraegerschaft} from './TSTraegerschaft';
import TSInstitution from './TSInstitution';

export default class TSUser {

    private _nachname: string;
    private _vorname: string;
    private _username: string;
    private _password: string;
    private _email: string;
    private _mandant: TSMandant;
    private _traegerschaft: TSTraegerschaft;
    private _institution: TSInstitution;
    private _role: TSRole;

    constructor(vorname?: string, nachname?: string, username?: string, password?: string, email?: string,
                mandant?: TSMandant, role?: TSRole, traegerschaft?: TSTraegerschaft, institution?: TSInstitution) {
        this._vorname = vorname;
        this._nachname = nachname;
        this._username = username;
        this._password = password;
        this._email = email;
        this._mandant = mandant;
        this._role = role;
        this._traegerschaft = traegerschaft;
        this._institution = institution;
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

    get traegerschaft(): TSTraegerschaft {
        return this._traegerschaft;
    }

    set traegerschaft(value: TSTraegerschaft) {
        this._traegerschaft = value;
    }

    get institution(): TSInstitution {
        return this._institution;
    }

    set institution(value: TSInstitution) {
        this._institution = value;
    }

    public getFullName(): string {
        return (this.vorname ? this.vorname : '') + ' ' + (this.nachname ? this.nachname : '');
    }

    getRoleKey(): string {
        return rolePrefix() + this.role;
    }
}
