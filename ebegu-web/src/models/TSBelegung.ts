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
import TSModul from './TSModul';
import * as moment from 'moment';

export default class TSBelegung extends TSAbstractEntity {

    private _module: TSModul[];
    private _eintrittsdatum: moment.Moment;

    constructor(module?: TSModul[], eintrittsdatum?: moment.Moment) {
        super();
        this._module = module;
        this._eintrittsdatum = eintrittsdatum;
    }

    public get module(): TSModul[] {
        return this._module;
    }

    public set module(value: TSModul[]) {
        this._module = value;
    }

    public get eintrittsdatum(): moment.Moment {
        return this._eintrittsdatum;
    }

    public set eintrittsdatum(value: moment.Moment) {
        this._eintrittsdatum = value;
    }
}
