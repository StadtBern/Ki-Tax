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

export default class TSLand {
    private _name: string;
    private _code: string;

    constructor(code: string, name: string) {
        this._name = name;
        this._code = code;
    }

    public set name(name: string) {
        this._name = name;
    }

    public set code(code: string) {
        this._code = code;
    }

    public get name(): string {
        return this._name;
    }

    public get code(): string {
        return this._code;
    }
}
