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
import TSEinkommensverschlechterung from './TSEinkommensverschlechterung';


export default class TSEinkommensverschlechterungContainer extends TSAbstractEntity {

    private _ekvGSBasisJahrPlus1: TSEinkommensverschlechterung;
    private _ekvGSBasisJahrPlus2: TSEinkommensverschlechterung;
    private _ekvJABasisJahrPlus1: TSEinkommensverschlechterung;
    private _ekvJABasisJahrPlus2: TSEinkommensverschlechterung;

    constructor(ekvGSBasisJahrPlus1?: TSEinkommensverschlechterung,
                ekvGSBasisJahrPlus2?: TSEinkommensverschlechterung,
                ekvJABasisJahrPlus1?: TSEinkommensverschlechterung,
                ekvJABasisJahrPlus2?: TSEinkommensverschlechterung) {
        super();
        this._ekvGSBasisJahrPlus1 = ekvGSBasisJahrPlus1;
        this._ekvGSBasisJahrPlus2 = ekvGSBasisJahrPlus2;
        this._ekvJABasisJahrPlus1 = ekvJABasisJahrPlus1;
        this._ekvJABasisJahrPlus2 = ekvJABasisJahrPlus2;
    }


    get ekvGSBasisJahrPlus1(): TSEinkommensverschlechterung {
        return this._ekvGSBasisJahrPlus1;
    }

    set ekvGSBasisJahrPlus1(value: TSEinkommensverschlechterung) {
        this._ekvGSBasisJahrPlus1 = value;
    }

    get ekvGSBasisJahrPlus2(): TSEinkommensverschlechterung {
        return this._ekvGSBasisJahrPlus2;
    }

    set ekvGSBasisJahrPlus2(value: TSEinkommensverschlechterung) {
        this._ekvGSBasisJahrPlus2 = value;
    }

    get ekvJABasisJahrPlus1(): TSEinkommensverschlechterung {
        return this._ekvJABasisJahrPlus1;
    }

    set ekvJABasisJahrPlus1(value: TSEinkommensverschlechterung) {
        this._ekvJABasisJahrPlus1 = value;
    }

    get ekvJABasisJahrPlus2(): TSEinkommensverschlechterung {
        return this._ekvJABasisJahrPlus2;
    }

    set ekvJABasisJahrPlus2(value: TSEinkommensverschlechterung) {
        this._ekvJABasisJahrPlus2 = value;
    }

    public isEmpty(): boolean {
        return !this._ekvJABasisJahrPlus1 && !this._ekvJABasisJahrPlus2;
    }
}
