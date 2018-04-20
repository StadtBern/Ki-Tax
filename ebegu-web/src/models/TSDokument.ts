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

import TSFile from './TSFile';
import * as moment from 'moment';
import TSUser from './TSUser';

export default class TSDokument extends TSFile {

    private _timestampUpload: moment.Moment;
    private _userUploaded: TSUser;

    constructor(timestampUpload?: moment.Moment, userUploaded?: TSUser) {
        super();
        this._timestampUpload = timestampUpload;
        this._userUploaded = userUploaded;
    }

    get timestampUpload(): moment.Moment {
        return this._timestampUpload;
    }

    set timestampUpload(value: moment.Moment) {
        this._timestampUpload = value;
    }

    get userUploaded(): TSUser {
        return this._userUploaded;
    }

    set userUploaded(value: TSUser) {
        this._userUploaded = value;
    }
}


