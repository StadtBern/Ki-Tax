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
import TSModulTagesschule from './TSModulTagesschule';

export default class TSBelegungTagesschule extends TSAbstractEntity {

    private _moduleTagesschule: TSModulTagesschule[] = [];
    private _eintrittsdatum: moment.Moment;
    private _planKlasse: string;

    constructor(moduleTagesschule?: TSModulTagesschule[], eintrittsdatum?: moment.Moment, planKlasse?: string) {
        super();
        this._moduleTagesschule = moduleTagesschule ? moduleTagesschule : [];
        this._eintrittsdatum = eintrittsdatum;
        this._planKlasse = planKlasse;
    }

    public get moduleTagesschule(): TSModulTagesschule[] {
        return this._moduleTagesschule;
    }

    public set moduleTagesschule(value: TSModulTagesschule[]) {
        this._moduleTagesschule = value;
    }

    public get eintrittsdatum(): moment.Moment {
        return this._eintrittsdatum;
    }

    public set eintrittsdatum(value: moment.Moment) {
        this._eintrittsdatum = value;
    }

    public get planKlasse(): string {
        return this._planKlasse;
    }

    public set planKlasse(value: string) {
        this._planKlasse = value;
    }
}
