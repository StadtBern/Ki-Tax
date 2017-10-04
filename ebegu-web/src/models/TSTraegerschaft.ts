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

import TSAbstractEntity from './TSAbstractEntity';

export class TSTraegerschaft extends TSAbstractEntity {

    private _name: string;
    private _active: boolean;
    private _mail: string;

    // just to communicate with client
    private _synchronizedWithOpenIdm: boolean = false;

    constructor(name?: string, active?: boolean, mail?: string) {
        super();
        this._name = name;
        this._active = active;
        this._mail = mail;
    }

    public get name(): string {
        return this._name;
    }

    public set name(value: string) {
        this._name = value;
    }

    get active(): boolean {
        return this._active;
    }

    set active(value: boolean) {
        this._active = value;
    }

    get synchronizedWithOpenIdm(): boolean {
        return this._synchronizedWithOpenIdm;
    }

    set synchronizedWithOpenIdm(value: boolean) {
        this._synchronizedWithOpenIdm = value;
    }

    get mail(): string {
        return this._mail;
    }

    set mail(value: string) {
        this._mail = value;
    }
}
