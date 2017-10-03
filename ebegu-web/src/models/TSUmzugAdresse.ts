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

import TSAdresse from './TSAdresse';
import {TSBetroffene} from './enums/TSBetroffene';
import TSAdresseContainer from './TSAdresseContainer';

export default class TSUmzugAdresse {

    private _betroffene: TSBetroffene;
    private _adresse: TSAdresseContainer;

    // nur zum speichern der anderen GS adresse
    private _adresseGS2: TSAdresseContainer;

    constructor(betroffene?: TSBetroffene, adresse?: TSAdresseContainer) {
        this._betroffene = betroffene;
        this._adresse = adresse;
    }

    get betroffene(): TSBetroffene {
        return this._betroffene;
    }

    set betroffene(value: TSBetroffene) {
        this._betroffene = value;
    }

    get adresse(): TSAdresseContainer {
        return this._adresse;
    }

    set adresse(value: TSAdresseContainer) {
        this._adresse = value;
    }

    get adresseGS2(): TSAdresseContainer {
        return this._adresseGS2;
    }

    set adresseGS2(value: TSAdresseContainer) {
        this._adresseGS2 = value;
    }
}
