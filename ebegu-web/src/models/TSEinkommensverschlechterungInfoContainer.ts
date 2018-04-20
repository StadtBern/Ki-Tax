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
import TSEinkommensverschlechterungInfo from './TSEinkommensverschlechterungInfo';

export default class TSEinkommensverschlechterungInfoContainer extends TSAbstractEntity {

    private _einkommensverschlechterungInfoGS: TSEinkommensverschlechterungInfo;

    private _einkommensverschlechterungInfoJA: TSEinkommensverschlechterungInfo = new TSEinkommensverschlechterungInfo;

    get einkommensverschlechterungInfoGS(): TSEinkommensverschlechterungInfo {
        return this._einkommensverschlechterungInfoGS;
    }

    set einkommensverschlechterungInfoGS(value: TSEinkommensverschlechterungInfo) {
        this._einkommensverschlechterungInfoGS = value;
    }

    get einkommensverschlechterungInfoJA(): TSEinkommensverschlechterungInfo {
        return this._einkommensverschlechterungInfoJA;
    }

    set einkommensverschlechterungInfoJA(value: TSEinkommensverschlechterungInfo) {
        this._einkommensverschlechterungInfoJA = value;
    }

    public init(): void {
        this.einkommensverschlechterungInfoJA = new TSEinkommensverschlechterungInfo();
        this.einkommensverschlechterungInfoJA.ekvFuerBasisJahrPlus1 = false;
        this.einkommensverschlechterungInfoJA.ekvFuerBasisJahrPlus2 = false;
    }
}
