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
import TSAbwesenheit from './TSAbwesenheit';

export default class TSAbwesenheitContainer extends TSAbstractEntity {

    private _abwesenheitGS: TSAbwesenheit;
    private _abwesenheitJA: TSAbwesenheit;

    constructor(abwesenheitGS?: TSAbwesenheit, abwesenheitJA?: TSAbwesenheit) {
        super();
        this._abwesenheitGS = abwesenheitGS;
        this._abwesenheitJA = abwesenheitJA;
    }

    get abwesenheitGS(): TSAbwesenheit {
        return this._abwesenheitGS;
    }

    set abwesenheitGS(value: TSAbwesenheit) {
        this._abwesenheitGS = value;
    }

    get abwesenheitJA(): TSAbwesenheit {
        return this._abwesenheitJA;
    }

    set abwesenheitJA(value: TSAbwesenheit) {
        this._abwesenheitJA = value;
    }
}
