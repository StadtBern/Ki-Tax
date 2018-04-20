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

import * as moment from 'moment';
import TSAbstractEntity from './TSAbstractEntity';

export default class TSBelegungFerieninselTag extends TSAbstractEntity {

    private _tag: moment.Moment;

    private _angemeldet: boolean; // Transient, wird nicht auf Server synchronisiert, bzw. nur die mit angemeldet=true


    constructor(tag?: moment.Moment) {
        super();
        this._tag = tag;
    }

    public get tag(): moment.Moment {
        return this._tag;
    }

    public set tag(value: moment.Moment) {
        this._tag = value;
    }

    public get angemeldet(): boolean {
        return this._angemeldet;
    }

    public set angemeldet(value: boolean) {
        this._angemeldet = value;
    }
}
