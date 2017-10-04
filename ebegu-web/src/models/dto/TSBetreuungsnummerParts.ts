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

/**
 * Hilfsdto welches verwendet werden kann um eine Betreuungsnummer in ihre subteile aufzuteilen
 */
export default class TSBetreuungsnummerParts {

    private _jahr: string;
    private _fallId: string;
    private _kindnummer: string;
    private _betreuungsnummer: string;

    constructor(jahr: string, fallId: string, kindnummer: string, betreuungsnummer: string) {
        this._jahr = jahr;
        this._fallId = fallId;
        this._kindnummer = kindnummer;
        this._betreuungsnummer = betreuungsnummer;
    }

    get jahr(): string {
        return this._jahr;
    }

    set jahr(value: string) {
        this._jahr = value;
    }

    get fallId(): string {
        return this._fallId;
    }

    set fallId(value: string) {
        this._fallId = value;
    }

    get kindnummer(): string {
        return this._kindnummer;
    }

    set kindnummer(value: string) {
        this._kindnummer = value;
    }

    get betreuungsnummer(): string {
        return this._betreuungsnummer;
    }

    set betreuungsnummer(value: string) {
        this._betreuungsnummer = value;
    }
}
