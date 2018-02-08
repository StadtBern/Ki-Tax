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

import TSMitteilung from './TSMitteilung';

export default class TSMtteilungSearchresultDTO {

    private _mitteilungen: Array<TSMitteilung>;
    private _totalResultSize: number;

    constructor(mitteilungen?: Array<TSMitteilung>, totalResultSize?: number) {
        this._mitteilungen = mitteilungen;
        this._totalResultSize = totalResultSize;
    }

    get mitteilungen(): Array<TSMitteilung> {
        return this._mitteilungen;
    }

    set mitteilungen(value: Array<TSMitteilung>) {
        this._mitteilungen = value;
    }

    get totalResultSize(): number {
        return this._totalResultSize;
    }

    set totalResultSize(value: number) {
        this._totalResultSize = value;
    }
}
