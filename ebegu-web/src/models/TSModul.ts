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
import {TSModulname} from './enums/TSModulname';
import {TSDayOfWeek} from './enums/TSDayOfWeek';
import * as moment from 'moment';

export default class TSModul extends TSAbstractEntity {

    private _wochentag: TSDayOfWeek;
    private _modulname: TSModulname;
    private _zeitVon: moment.Moment;
    private _zeitBis: moment.Moment;

    public get wochentag(): TSDayOfWeek {
        return this._wochentag;
    }

    public set wochentag(value: TSDayOfWeek) {
        this._wochentag = value;
    }

    public get modulname(): TSModulname {
        return this._modulname;
    }

    public set modulname(value: TSModulname) {
        this._modulname = value;
    }

    public get zeitVon(): moment.Moment {
        return this._zeitVon;
    }

    public set zeitVon(value: moment.Moment) {
        this._zeitVon = value;
    }

    public get zeitBis(): moment.Moment {
        return this._zeitBis;
    }

    public set zeitBis(value: moment.Moment) {
        this._zeitBis = value;
    }
}
