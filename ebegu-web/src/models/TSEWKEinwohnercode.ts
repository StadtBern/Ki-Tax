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
import * as moment from 'moment';

/**
 * DTO für einen Einwohnercode aus dem EWK
 */
export default class TSEWKEinwohnercode extends TSAbstractEntity {

    private _code: string;
    private _codeTxt: string;
    private _gueltigVon: moment.Moment;
    private _gueltigBis: moment.Moment;

    constructor(code?: string, codeTxt?: string, gueltigVon?: moment.Moment, gueltigBis?: moment.Moment) {
        super();
        this._code = code;
        this._codeTxt = codeTxt;
        this._gueltigVon = gueltigVon;
        this._gueltigBis = gueltigBis;
    }

    get code(): string {
        return this._code;
    }

    set code(value: string) {
        this._code = value;
    }

    get codeTxt(): string {
        return this._codeTxt;
    }

    set codeTxt(value: string) {
        this._codeTxt = value;
    }

    get gueltigVon(): moment.Moment {
        return this._gueltigVon;
    }

    set gueltigVon(value: moment.Moment) {
        this._gueltigVon = value;
    }

    get gueltigBis(): moment.Moment {
        return this._gueltigBis;
    }

    set gueltigBis(value: moment.Moment) {
        this._gueltigBis = value;
    }
}
