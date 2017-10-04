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

import TSGesuchsperiode from './TSGesuchsperiode';
import {TSBetreuungsangebotTyp} from './enums/TSBetreuungsangebotTyp';
import TSInstitution from './TSInstitution';
import * as moment from 'moment';

export default class TSPendenzInstitution {

    private _betreuungsNummer: string;
    private _betreuungsId: string;
    private _gesuchId: string;
    private _kindId: string;
    private _name: string;
    private _vorname: string;
    private _geburtsdatum: moment.Moment;
    private _typ: string;
    private _gesuchsperiode: TSGesuchsperiode;
    private _eingangsdatum: moment.Moment;
    private _eingangsdatumSTV: moment.Moment;
    private _betreuungsangebotTyp: TSBetreuungsangebotTyp;
    private _institution: TSInstitution;

    constructor(betreuungsNummer?: string, betreuungsId?: string, gesuchId?: string, kindId?: string, name?: string, vorname?: string,
                geburtsdatum?: moment.Moment, typ?: string, gesuchsperiode?: TSGesuchsperiode, eingangsdatum?: moment.Moment,
                eingangsdatumSTV?: moment.Moment, betreuungsangebotTyp?: TSBetreuungsangebotTyp, institution?: TSInstitution) {
        this._betreuungsNummer = betreuungsNummer;
        this._betreuungsId = betreuungsId;
        this._gesuchId = gesuchId;
        this._kindId = kindId;
        this._name = name;
        this._vorname = vorname;
        this._geburtsdatum = geburtsdatum;
        this._typ = typ;
        this._gesuchsperiode = gesuchsperiode;
        this._eingangsdatum = eingangsdatum;
        this._eingangsdatumSTV = eingangsdatumSTV;
        this._betreuungsangebotTyp = betreuungsangebotTyp;
        this._institution = institution;
    }

    get betreuungsNummer(): string {
        return this._betreuungsNummer;
    }

    set betreuungsNummer(value: string) {
        this._betreuungsNummer = value;
    }

    get betreuungsId(): string {
        return this._betreuungsId;
    }

    set betreuungsId(value: string) {
        this._betreuungsId = value;
    }

    get gesuchId(): string {
        return this._gesuchId;
    }

    set gesuchId(value: string) {
        this._gesuchId = value;
    }

    get kindId(): string {
        return this._kindId;
    }

    set kindId(value: string) {
        this._kindId = value;
    }

    get name(): string {
        return this._name;
    }

    set name(value: string) {
        this._name = value;
    }

    get vorname(): string {
        return this._vorname;
    }

    set vorname(value: string) {
        this._vorname = value;
    }

    get geburtsdatum(): moment.Moment {
        return this._geburtsdatum;
    }

    set geburtsdatum(value: moment.Moment) {
        this._geburtsdatum = value;
    }

    get typ(): string {
        return this._typ;
    }

    set typ(value: string) {
        this._typ = value;
    }

    get gesuchsperiode(): TSGesuchsperiode {
        return this._gesuchsperiode;
    }

    set gesuchsperiode(value: TSGesuchsperiode) {
        this._gesuchsperiode = value;
    }

    get eingangsdatum(): moment.Moment {
        return this._eingangsdatum;
    }

    set eingangsdatum(value: moment.Moment) {
        this._eingangsdatum = value;
    }

    get eingangsdatumSTV(): moment.Moment {
        return this._eingangsdatumSTV;
    }

    set eingangsdatumSTV(value: moment.Moment) {
        this._eingangsdatumSTV = value;
    }

    get betreuungsangebotTyp(): TSBetreuungsangebotTyp {
        return this._betreuungsangebotTyp;
    }

    set betreuungsangebotTyp(value: TSBetreuungsangebotTyp) {
        this._betreuungsangebotTyp = value;
    }

    get institution(): TSInstitution {
        return this._institution;
    }

    set institution(value: TSInstitution) {
        this._institution = value;
    }
}
