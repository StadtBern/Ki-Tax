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

import EbeguUtil from '../utils/EbeguUtil';

export default class TSAbstractAntragDTO {

    private _fallNummer: number;
    private _familienName: string;

    constructor(fallNummer?: number, familienName?: string) {
        this._fallNummer = fallNummer;
        this._familienName = familienName;
    }

    get fallNummer(): number {
        return this._fallNummer;
    }

    set fallNummer(value: number) {
        this._fallNummer = value;
    }

    get familienName(): string {
        return this._familienName;
    }

    set familienName(value: string) {
        this._familienName = value;
    }

    public getQuicksearchString(): string {
        let text = '';
        if (this.fallNummer) {
            text = EbeguUtil.addZerosToNumber(this.fallNummer, 6);
        }
        if (this.familienName) {
            text = text + ' ' + this.familienName;
        }
        return text;
    }

}
