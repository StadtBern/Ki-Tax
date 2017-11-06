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

import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';
import {TSDateRange} from './types/TSDateRange';
import {TSGesuchsperiodeStatus} from './enums/TSGesuchsperiodeStatus';
import * as moment from 'moment';

export default class TSGesuchsperiode extends TSAbstractDateRangedEntity {

    private _status: TSGesuchsperiodeStatus;
    private _datumFreischaltungTagesschule: moment.Moment;
    private _datumErsterSchultag: moment.Moment;

    constructor(status?: TSGesuchsperiodeStatus, gueltigkeit?: TSDateRange, datumFreischaltungTagesschule?: moment.Moment, datumErsterSchultag?: moment.Moment) {
        super(gueltigkeit);
        this._status = status;
        this._datumFreischaltungTagesschule = datumFreischaltungTagesschule;
        this._datumErsterSchultag = datumErsterSchultag;
    }

    get status(): TSGesuchsperiodeStatus {
        return this._status;
    }

    set status(value: TSGesuchsperiodeStatus) {
        this._status = value;
    }

    get datumFreischaltungTagesschule(): moment.Moment {
        return this._datumFreischaltungTagesschule;
    }

    set datumFreischaltungTagesschule(value: moment.Moment) {
        this._datumFreischaltungTagesschule = value;
    }

    get datumErsterSchultag(): moment.Moment {
        return this._datumErsterSchultag;
    }

    set datumErsterSchultag(value: moment.Moment) {
        this._datumErsterSchultag = value;
    }

    get gesuchsperiodeString(): string {
        if (this.gueltigkeit && this.gueltigkeit.gueltigAb && this.gueltigkeit.gueltigBis) {
            return this.gueltigkeit.gueltigAb.year() + '/'
                + (this.gueltigkeit.gueltigBis.year() - 2000);
        }
        return undefined;
    }

    isTagesschulenAnmeldungKonfiguriert(): boolean {
        return this.hasTagesschulenAnmeldung()
            && this.datumFreischaltungTagesschule.isBefore(this.gueltigkeit.gueltigAb);
    }

    isTageschulenAnmeldungAktiv(): boolean {
        return this.isTagesschulenAnmeldungKonfiguriert() && this.datumFreischaltungTagesschule.isBefore(moment());
    }

    hasTagesschulenAnmeldung(): boolean {
        return this._datumFreischaltungTagesschule !== null && this.datumFreischaltungTagesschule !== undefined;
    }

    isEntwurf(): boolean {
        return this.status === TSGesuchsperiodeStatus.ENTWURF;
    }
}
