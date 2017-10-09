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
import {TSZahlungsstatus} from './enums/TSZahlungsstatus';

export default class TSZahlung extends TSAbstractEntity {

    private _institutionsName: string;

    private _status: TSZahlungsstatus;

    private _betragTotalZahlung: number;

    constructor(institutionsName?: string, status?: TSZahlungsstatus, betragTotalZahlung?: number) {
        super();
        this._institutionsName = institutionsName;
        this._status = status;
        this._betragTotalZahlung = betragTotalZahlung;
    }

    get institutionsName(): string {
        return this._institutionsName;
    }

    set institutionsName(value: string) {
        this._institutionsName = value;
    }

    get status(): TSZahlungsstatus {
        return this._status;
    }

    set status(value: TSZahlungsstatus) {
        this._status = value;
    }

    get betragTotalZahlung(): number {
        return this._betragTotalZahlung;
    }

    set betragTotalZahlung(value: number) {
        this._betragTotalZahlung = value;
    }
}


