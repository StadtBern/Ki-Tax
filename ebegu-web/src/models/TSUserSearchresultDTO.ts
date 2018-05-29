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

import TSUser from './TSUser';

export default class TSUserSearchresultDTO {

    private _userDTOs: Array<TSUser>;
    private _totalResultSize: number;

    constructor(userDTOs?: Array<TSUser>, totalResultSize?: number) {
        this._userDTOs = userDTOs;
        this._totalResultSize = totalResultSize;
    }

    get userDTOs(): Array<TSUser> {
        return this._userDTOs;
    }

    set userDTOs(value: Array<TSUser>) {
        this._userDTOs = value;
    }

    get totalResultSize(): number {
        return this._totalResultSize;
    }

    set totalResultSize(value: number) {
        this._totalResultSize = value;
    }
}
