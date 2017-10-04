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
import TSFamiliensituation from './TSFamiliensituation';

export default class TSFamiliensituationContainer extends TSAbstractEntity {

    private _familiensituationJA: TSFamiliensituation;
    private _familiensituationGS: TSFamiliensituation;
    private _familiensituationErstgesuch: TSFamiliensituation;


    constructor(familiensituationJA?: TSFamiliensituation, familiensituationGS?: TSFamiliensituation,
                familiensituationErstgesuch?: TSFamiliensituation) {
        super();
        this._familiensituationJA = familiensituationJA;
        this._familiensituationGS = familiensituationGS;
        this._familiensituationErstgesuch = familiensituationErstgesuch;
    }

    get familiensituationJA(): TSFamiliensituation {
        return this._familiensituationJA;
    }

    set familiensituationJA(value: TSFamiliensituation) {
        this._familiensituationJA = value;
    }

    get familiensituationGS(): TSFamiliensituation {
        return this._familiensituationGS;
    }

    set familiensituationGS(value: TSFamiliensituation) {
        this._familiensituationGS = value;
    }

    get familiensituationErstgesuch(): TSFamiliensituation {
        return this._familiensituationErstgesuch;
    }

    set familiensituationErstgesuch(value: TSFamiliensituation) {
        this._familiensituationErstgesuch = value;
    }
}
