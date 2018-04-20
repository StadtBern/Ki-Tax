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
import TSAdresse from './TSAdresse';

export default class TSAdresseContainer extends TSAbstractEntity {

    private _adresseJA: TSAdresse;
    private _adresseGS: TSAdresse;
    private _showDatumVon: boolean;

    constructor(adresseJA?: TSAdresse, adresseGS?: TSAdresse) {
        super();
        this._adresseGS = adresseGS;
        this._adresseJA = adresseJA;
    }

    get adresseJA(): TSAdresse {
        return this._adresseJA;
    }

    set adresseJA(value: TSAdresse) {
        this._adresseJA = value;
    }

    get adresseGS(): TSAdresse {
        return this._adresseGS;
    }

    set adresseGS(value: TSAdresse) {
        this._adresseGS = value;
    }

    public get showDatumVon(): boolean {
        return this._showDatumVon;
    }

    public set showDatumVon(value: boolean) {
        this._showDatumVon = value;
    }

    public isSameWohnAdresse(umzugAdresse: TSAdresseContainer): boolean {
        if (this.adresseJA && umzugAdresse.adresseJA) {
            return this.adresseJA.isSameWohnAdresse(umzugAdresse.adresseJA);
        }
        return undefined;
    }
}
