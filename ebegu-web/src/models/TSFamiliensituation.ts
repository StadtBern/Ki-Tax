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

import {TSFamilienstatus} from './enums/TSFamilienstatus';
import {TSGesuchstellerKardinalitaet} from './enums/TSGesuchstellerKardinalitaet';
import TSAbstractEntity from './TSAbstractEntity';
import * as moment from 'moment';

export default class TSFamiliensituation extends TSAbstractEntity {

    private _familienstatus: TSFamilienstatus;
    private _gesuchstellerKardinalitaet: TSGesuchstellerKardinalitaet;
    private _gemeinsameSteuererklaerung: boolean;
    private _aenderungPer: moment.Moment;
    private _sozialhilfeBezueger: boolean;
    private _verguenstigungGewuenscht: boolean;

    constructor(familienstatus?: TSFamilienstatus, gesuchstellerKardinalitaet?: TSGesuchstellerKardinalitaet,
                gemeinsameSteuererklaerung?: boolean, aenderungPer?: moment.Moment, sozialhilfeBezueger?: boolean,
                verguenstigungGewuenscht?: boolean) {
        super();
        this._familienstatus = familienstatus;
        this._gesuchstellerKardinalitaet = gesuchstellerKardinalitaet;
        this._gemeinsameSteuererklaerung = gemeinsameSteuererklaerung;
        this._aenderungPer = aenderungPer;
        this._sozialhilfeBezueger = sozialhilfeBezueger;
        this._verguenstigungGewuenscht = verguenstigungGewuenscht;
    }

    public get familienstatus(): TSFamilienstatus {
        return this._familienstatus;
    }

    public set familienstatus(familienstatus: TSFamilienstatus) {
        this._familienstatus = familienstatus;
    }

    public get gesuchstellerKardinalitaet(): TSGesuchstellerKardinalitaet {
        return this._gesuchstellerKardinalitaet;
    }

    public set gesuchstellerKardinalitaet(gesuchstellerKardinalitaet: TSGesuchstellerKardinalitaet) {
        this._gesuchstellerKardinalitaet = gesuchstellerKardinalitaet;
    }

    get gemeinsameSteuererklaerung(): boolean {
        return this._gemeinsameSteuererklaerung;
    }

    set gemeinsameSteuererklaerung(value: boolean) {
        this._gemeinsameSteuererklaerung = value;
    }

    get aenderungPer(): moment.Moment {
        return this._aenderungPer;
    }

    set aenderungPer(value: moment.Moment) {
        this._aenderungPer = value;
    }

    public get sozialhilfeBezueger(): boolean {
        return this._sozialhilfeBezueger;
    }

    public set sozialhilfeBezueger(value: boolean) {
        this._sozialhilfeBezueger = value;
    }

    public get verguenstigungGewuenscht(): boolean {
        return this._verguenstigungGewuenscht;
    }

    public set verguenstigungGewuenscht(value: boolean) {
        this._verguenstigungGewuenscht = value;
    }

    public hasSecondGesuchsteller(): boolean {
        switch (this.familienstatus) {
            case TSFamilienstatus.ALLEINERZIEHEND:
            case TSFamilienstatus.WENIGER_FUENF_JAHRE:
                return TSGesuchstellerKardinalitaet.ZU_ZWEIT === this.gesuchstellerKardinalitaet;
            case TSFamilienstatus.VERHEIRATET:
            case TSFamilienstatus.KONKUBINAT:
            case TSFamilienstatus.LAENGER_FUENF_JAHRE:
                return true;
            default:
                //wir sollten hier nie hinkommen
                return false;
        }
    }

    public isSameFamiliensituation(other: TSFamiliensituation) {
        return this.familienstatus === other.familienstatus && this.gesuchstellerKardinalitaet === other.gesuchstellerKardinalitaet;
    }

    public revertFamiliensituation(other: TSFamiliensituation) {
        this.familienstatus = other.familienstatus;
        this.gesuchstellerKardinalitaet = other.gesuchstellerKardinalitaet;
    }

}
