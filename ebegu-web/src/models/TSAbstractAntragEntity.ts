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

import TSFall from './TSFall';
import TSAbstractEntity from './TSAbstractEntity';
import TSGesuchsperiode from './TSGesuchsperiode';
import {TSAntragStatus} from './enums/TSAntragStatus';
import {TSAntragTyp} from './enums/TSAntragTyp';
import {TSEingangsart} from './enums/TSEingangsart';
import * as moment from 'moment';

export default class TSAbstractAntragEntity extends TSAbstractEntity {

    private _fall: TSFall;
    private _gesuchsperiode: TSGesuchsperiode;
    private _eingangsdatum: moment.Moment;
    private _freigabeDatum: moment.Moment;
    private _status: TSAntragStatus;
    private _typ: TSAntragTyp;
    private _eingangsart: TSEingangsart;

    public get fall(): TSFall {
        return this._fall;
    }

    public set fall(value: TSFall) {
        this._fall = value;
    }

    get gesuchsperiode(): TSGesuchsperiode {
        return this._gesuchsperiode;
    }

    set gesuchsperiode(gesuchsperiode: TSGesuchsperiode) {
        this._gesuchsperiode = gesuchsperiode;
    }

    get eingangsdatum(): moment.Moment {
        return this._eingangsdatum;
    }

    set eingangsdatum(value: moment.Moment) {
        this._eingangsdatum = value;
    }

    get freigabeDatum(): moment.Moment {
        return this._freigabeDatum;
    }

    set freigabeDatum(value: moment.Moment) {
        this._freigabeDatum = value;
    }

    get status(): TSAntragStatus {
        return this._status;
    }

    set status(value: TSAntragStatus) {
        this._status = value;
    }

    get typ(): TSAntragTyp {
        return this._typ;
    }

    set typ(value: TSAntragTyp) {
        this._typ = value;
    }

    get eingangsart(): TSEingangsart {
        return this._eingangsart;
    }

    set eingangsart(value: TSEingangsart) {
        this._eingangsart = value;
    }
}
