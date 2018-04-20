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

import {TSAbstractPensumEntity} from './TSAbstractPensumEntity';
import {TSTaetigkeit} from './enums/TSTaetigkeit';
import {TSZuschlagsgrund} from './enums/TSZuschlagsgrund';
import {TSDateRange} from './types/TSDateRange';

/**
 * Definiert ein Erwerbspensum
 */
export default class TSErwerbspensum extends TSAbstractPensumEntity {

    private _taetigkeit: TSTaetigkeit;

    private _zuschlagZuErwerbspensum: boolean;

    private _zuschlagsgrund: TSZuschlagsgrund;

    private _zuschlagsprozent: number;

    private _bezeichnung: String;

    constructor(pensum?: number, gueltigkeit?: TSDateRange, taetigkeit?: TSTaetigkeit, zuschlagZuErwerbspensum?: boolean,
                zuschlagsgrund?: TSZuschlagsgrund, zuschlagsprozent?: number) {
        super(pensum, gueltigkeit);
        this._taetigkeit = taetigkeit;
        this._zuschlagZuErwerbspensum = zuschlagZuErwerbspensum;
        this._zuschlagsgrund = zuschlagsgrund;
        this._zuschlagsprozent = zuschlagsprozent;
    }

    get taetigkeit(): TSTaetigkeit {
        return this._taetigkeit;
    }

    set taetigkeit(value: TSTaetigkeit) {
        this._taetigkeit = value;
    }

    get zuschlagZuErwerbspensum(): boolean {
        return this._zuschlagZuErwerbspensum;
    }

    set zuschlagZuErwerbspensum(value: boolean) {
        this._zuschlagZuErwerbspensum = value;
    }

    get zuschlagsgrund(): TSZuschlagsgrund {
        return this._zuschlagsgrund;
    }

    set zuschlagsgrund(value: TSZuschlagsgrund) {
        this._zuschlagsgrund = value;
    }

    get zuschlagsprozent(): number {
        return this._zuschlagsprozent;
    }

    set zuschlagsprozent(value: number) {
        this._zuschlagsprozent = value;
    }

    get bezeichnung(): String {
        return this._bezeichnung;
    }

    set bezeichnung(value: String) {
        this._bezeichnung = value;
    }
}
