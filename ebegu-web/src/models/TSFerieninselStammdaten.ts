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
import {TSFerienname} from './enums/TSFerienname';
import * as moment from 'moment';
import TSGesuchsperiode from './TSGesuchsperiode';
import TSFerieninselZeitraum from './TSFerieninselZeitraum';

export default class TSFerieninselStammdaten extends TSAbstractEntity {

    private _ferienname: TSFerienname;
    private _zeitraumList: Array<TSFerieninselZeitraum>;
    private _anmeldeschluss: moment.Moment;
    private _gesuchsperiode: TSGesuchsperiode;


    constructor(ferienname?: TSFerienname, zeitraumList?: Array<TSFerieninselZeitraum>, anmeldeschluss?: moment.Moment, gesuchsperiode?: TSGesuchsperiode) {
        super();
        this._ferienname = ferienname;
        this._zeitraumList = zeitraumList;
        this._anmeldeschluss = anmeldeschluss;
        this._gesuchsperiode = gesuchsperiode;
    }


    public get ferienname(): TSFerienname {
        return this._ferienname;
    }

    public set ferienname(value: TSFerienname) {
        this._ferienname = value;
    }

    public get zeitraumList(): Array<TSFerieninselZeitraum> {
        return this._zeitraumList;
    }

    public set zeitraumList(value: Array<TSFerieninselZeitraum>) {
        this._zeitraumList = value;
    }

    public get anmeldeschluss(): moment.Moment {
        return this._anmeldeschluss;
    }

    public set anmeldeschluss(value: moment.Moment) {
        this._anmeldeschluss = value;
    }

    public get gesuchsperiode(): TSGesuchsperiode {
        return this._gesuchsperiode;
    }

    public set gesuchsperiode(value: TSGesuchsperiode) {
        this._gesuchsperiode = value;
    }
}
