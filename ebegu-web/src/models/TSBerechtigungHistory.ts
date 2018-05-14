/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

import {TSRole} from './enums/TSRole';
import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';
import TSInstitution from './TSInstitution';
import {TSTraegerschaft} from './TSTraegerschaft';
import TSUser from './TSUser';

export default class TSBerechtigungHistory extends TSAbstractDateRangedEntity {

    private _userErstellt: string;
    private _user: TSUser;
    private _role: TSRole;
    private _traegerschaft: TSTraegerschaft;
    private _institution: TSInstitution;
    private _gesperrt: boolean;
    private _geloescht: boolean;

    public get userErstellt(): string {
        return this._userErstellt;
    }

    public set userErstellt(value: string) {
        this._userErstellt = value;
    }

    public get user(): TSUser {
        return this._user;
    }

    public set user(value: TSUser) {
        this._user = value;
    }

    public get role(): TSRole {
        return this._role;
    }

    public set role(value: TSRole) {
        this._role = value;
    }

    public get traegerschaft(): TSTraegerschaft {
        return this._traegerschaft;
    }

    public set traegerschaft(value: TSTraegerschaft) {
        this._traegerschaft = value;
    }

    public get institution(): TSInstitution {
        return this._institution;
    }

    public set institution(value: TSInstitution) {
        this._institution = value;
    }

    public get gesperrt(): boolean {
        return this._gesperrt;
    }

    public set gesperrt(value: boolean) {
        this._gesperrt = value;
    }

    public get geloescht(): boolean {
        return this._geloescht;
    }

    public set geloescht(value: boolean) {
        this._geloescht = value;
    }

    public getInstitutionOrTraegerschaft(): string {
        if (this.institution) {
            return this.institution.name;
        } else if (this.traegerschaft) {
            return this.traegerschaft.name;
        } else {
            return '';
        }
    }
}
