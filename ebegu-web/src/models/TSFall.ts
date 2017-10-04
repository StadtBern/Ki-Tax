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
import TSUser from './TSUser';

export default class TSFall extends TSAbstractEntity {

    private _fallNummer: number;
    private _nextNumberKind: number;
    private _verantwortlicher: TSUser;
    private _besitzer: TSUser;

    constructor(fallNummer?: number, verantwortlicher?: TSUser, nextNumberKind?: number, besitzer?: TSUser) {
        super();
        this._fallNummer = fallNummer;
        this._verantwortlicher = verantwortlicher;
        this._nextNumberKind = nextNumberKind;
        this._besitzer = besitzer;
    }

    get fallNummer(): number {
        return this._fallNummer;
    }

    set fallNummer(value: number) {
        this._fallNummer = value;
    }

    get verantwortlicher(): TSUser {
        return this._verantwortlicher;
    }

    set verantwortlicher(value: TSUser) {
        this._verantwortlicher = value;
    }

    get nextNumberKind(): number {
        return this._nextNumberKind;
    }

    set nextNumberKind(value: number) {
        this._nextNumberKind = value;
    }

    get besitzer(): TSUser {
        return this._besitzer;
    }

    set besitzer(value: TSUser) {
        this._besitzer = value;
    }
}
