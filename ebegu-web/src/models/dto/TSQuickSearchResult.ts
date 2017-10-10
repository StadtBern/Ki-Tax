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

import TSSearchResultEntry from './TSSearchResultEntry';

export default class TSQuickSearchResult {


    private _resultEntities: Array<TSSearchResultEntry>;
    private _totalResultSize: number;


    constructor(antragDTOs?: Array<TSSearchResultEntry>, totalResultSize?: number) {
        this._resultEntities = antragDTOs;
        this._totalResultSize = totalResultSize;
    }


    get resultEntities(): Array<TSSearchResultEntry> {
        return this._resultEntities;
    }

    set resultEntities(value: Array<TSSearchResultEntry>) {
        this._resultEntities = value;
    }

    get totalResultSize(): number {
        return this._totalResultSize;
    }

    set totalResultSize(value: number) {
        this._totalResultSize = value;
    }
}
