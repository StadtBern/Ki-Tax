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
import {TSEbeguVorlageKey} from './enums/TSEbeguVorlageKey';
import TSVorlage from './TSVorlage';

export default class TSEbeguVorlage extends TSAbstractDateRangedEntity {

    private _name: TSEbeguVorlageKey;
    private _vorlage: TSVorlage;
    private _proGesuchsperiode: boolean;

    constructor(name?: TSEbeguVorlageKey, vorlage?: TSVorlage, gueltigkeit?: TSDateRange, proGesuchsperiode?: boolean) {
        super(gueltigkeit);
        this._name = name;
        this._vorlage = vorlage;
        this._proGesuchsperiode = proGesuchsperiode;
    }

    get name(): TSEbeguVorlageKey {
        return this._name;
    }

    set name(value: TSEbeguVorlageKey) {
        this._name = value;
    }

    get vorlage(): TSVorlage {
        return this._vorlage;
    }

    set vorlage(value: TSVorlage) {
        this._vorlage = value;
    }

    get proGesuchsperiode(): boolean {
        return this._proGesuchsperiode;
    }

    set proGesuchsperiode(value: boolean) {
        this._proGesuchsperiode = value;
    }
}
