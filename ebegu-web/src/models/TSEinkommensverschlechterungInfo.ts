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

import * as moment from 'moment';
import TSAbstractEntity from './TSAbstractEntity';

export default class TSEinkommensverschlechterungInfo extends TSAbstractEntity {

    private _einkommensverschlechterung: boolean = false;

    private _ekvFuerBasisJahrPlus1: boolean;
    private _ekvFuerBasisJahrPlus2: boolean;

    private _grundFuerBasisJahrPlus1: string;
    private _grundFuerBasisJahrPlus2: string;

    private _stichtagFuerBasisJahrPlus1: moment.Moment;
    private _stichtagFuerBasisJahrPlus2: moment.Moment;

    private _gemeinsameSteuererklaerung_BjP1: boolean;
    private _gemeinsameSteuererklaerung_BjP2: boolean;

    private _ekvBasisJahrPlus1Annulliert: boolean = false;
    private _ekvBasisJahrPlus2Annulliert: boolean = false;


    get einkommensverschlechterung(): boolean {
        return this._einkommensverschlechterung;
    }

    set einkommensverschlechterung(value: boolean) {
        this._einkommensverschlechterung = value;
    }

    get ekvFuerBasisJahrPlus1(): boolean {
        return this._ekvFuerBasisJahrPlus1;
    }

    set ekvFuerBasisJahrPlus1(value: boolean) {
        this._ekvFuerBasisJahrPlus1 = value;
    }

    get ekvFuerBasisJahrPlus2(): boolean {
        return this._ekvFuerBasisJahrPlus2;
    }

    set ekvFuerBasisJahrPlus2(value: boolean) {
        this._ekvFuerBasisJahrPlus2 = value;
    }

    get grundFuerBasisJahrPlus1(): string {
        return this._grundFuerBasisJahrPlus1;
    }

    set grundFuerBasisJahrPlus1(value: string) {
        this._grundFuerBasisJahrPlus1 = value;
    }

    get grundFuerBasisJahrPlus2(): string {
        return this._grundFuerBasisJahrPlus2;
    }

    set grundFuerBasisJahrPlus2(value: string) {
        this._grundFuerBasisJahrPlus2 = value;
    }

    get stichtagFuerBasisJahrPlus1(): moment.Moment {
        return this._stichtagFuerBasisJahrPlus1;
    }

    set stichtagFuerBasisJahrPlus1(value: moment.Moment) {
        this._stichtagFuerBasisJahrPlus1 = value;
    }

    get stichtagFuerBasisJahrPlus2(): moment.Moment {
        return this._stichtagFuerBasisJahrPlus2;
    }

    set stichtagFuerBasisJahrPlus2(value: moment.Moment) {
        this._stichtagFuerBasisJahrPlus2 = value;
    }

    get gemeinsameSteuererklaerung_BjP1(): boolean {
        return this._gemeinsameSteuererklaerung_BjP1;
    }

    set gemeinsameSteuererklaerung_BjP1(value: boolean) {
        this._gemeinsameSteuererklaerung_BjP1 = value;
    }

    get gemeinsameSteuererklaerung_BjP2(): boolean {
        return this._gemeinsameSteuererklaerung_BjP2;
    }

    set gemeinsameSteuererklaerung_BjP2(value: boolean) {
        this._gemeinsameSteuererklaerung_BjP2 = value;
    }

    get ekvBasisJahrPlus1Annulliert(): boolean {
        return this._ekvBasisJahrPlus1Annulliert;
    }

    set ekvBasisJahrPlus1Annulliert(value: boolean) {
        this._ekvBasisJahrPlus1Annulliert = value;
    }

    get ekvBasisJahrPlus2Annulliert(): boolean {
        return this._ekvBasisJahrPlus2Annulliert;
    }

    set ekvBasisJahrPlus2Annulliert(value: boolean) {
        this._ekvBasisJahrPlus2Annulliert = value;
    }
}
