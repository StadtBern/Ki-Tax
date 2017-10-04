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

import TSAbstractFinanzielleSituation from './TSAbstractFinanzielleSituation';

export default class TSFinanzielleSituation extends TSAbstractFinanzielleSituation {

    private _nettolohn: number;
    private _geschaeftsgewinnBasisjahrMinus2: number;
    private _geschaeftsgewinnBasisjahrMinus1: number;


    constructor(steuerveranlagungErhalten?: boolean, steuererklaerungAusgefuellt?: boolean, nettolohn?: number,
                familienzulage?: number, ersatzeinkommen?: number, erhalteneAlimente?: number, bruttovermoegen?: number,
                schulden?: number, geschaeftsgewinnBasisjahrMinus2?: number,
                geschaeftsgewinnBasisjahrMinus1?: number, geschaeftsgewinnBasisjahr?: number, geleisteteAlimente?: number) {
        super(steuerveranlagungErhalten, steuererklaerungAusgefuellt,
            familienzulage, ersatzeinkommen, erhalteneAlimente, bruttovermoegen,
            schulden, geschaeftsgewinnBasisjahr, geleisteteAlimente);
        this._nettolohn = nettolohn;
        this._geschaeftsgewinnBasisjahrMinus2 = geschaeftsgewinnBasisjahrMinus2;
        this._geschaeftsgewinnBasisjahrMinus1 = geschaeftsgewinnBasisjahrMinus1;
    }


    get nettolohn(): number {
        return this._nettolohn;
    }

    set nettolohn(value: number) {
        this._nettolohn = value;
    }

    get geschaeftsgewinnBasisjahrMinus2(): number {
        return this._geschaeftsgewinnBasisjahrMinus2;
    }

    set geschaeftsgewinnBasisjahrMinus2(value: number) {
        this._geschaeftsgewinnBasisjahrMinus2 = value;
    }

    get geschaeftsgewinnBasisjahrMinus1(): number {
        return this._geschaeftsgewinnBasisjahrMinus1;
    }

    set geschaeftsgewinnBasisjahrMinus1(value: number) {
        this._geschaeftsgewinnBasisjahrMinus1 = value;
    }

    public isSelbstaendig(): boolean {
        return (this.geschaeftsgewinnBasisjahr !== null && this.geschaeftsgewinnBasisjahr !== undefined)
            || (this._geschaeftsgewinnBasisjahrMinus1 !== null && this._geschaeftsgewinnBasisjahrMinus1 !== undefined)
            || (this._geschaeftsgewinnBasisjahrMinus2 !== null && this._geschaeftsgewinnBasisjahrMinus2 !== undefined);
    }
}
