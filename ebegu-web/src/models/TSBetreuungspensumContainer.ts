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
import TSBetreuungspensum from './TSBetreuungspensum';

export default class TSBetreuungspensumContainer extends TSAbstractEntity {

    private _betreuungspensumGS: TSBetreuungspensum;
    private _betreuungspensumJA: TSBetreuungspensum;

    constructor(betreuungspensumGS?: TSBetreuungspensum, betreuungspensumJA?: TSBetreuungspensum) {
        super();
        this._betreuungspensumGS = betreuungspensumGS;
        this._betreuungspensumJA = betreuungspensumJA;
    }

    get betreuungspensumGS(): TSBetreuungspensum {
        return this._betreuungspensumGS;
    }

    set betreuungspensumGS(value: TSBetreuungspensum) {
        this._betreuungspensumGS = value;
    }

    get betreuungspensumJA(): TSBetreuungspensum {
        return this._betreuungspensumJA;
    }

    set betreuungspensumJA(value: TSBetreuungspensum) {
        this._betreuungspensumJA = value;
    }
}
