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

import TSAbstractPersonEntity from './TSAbstractPersonEntity';
import {TSGeschlecht} from './enums/TSGeschlecht';
import {TSPensumFachstelle} from './TSPensumFachstelle';
import {TSKinderabzug} from './enums/TSKinderabzug';
import * as moment from 'moment';

export default class TSKind extends TSAbstractPersonEntity {

    private _wohnhaftImGleichenHaushalt: number;
    private _kinderabzug: TSKinderabzug;
    private _familienErgaenzendeBetreuung: boolean;
    private _mutterspracheDeutsch: boolean;
    private _einschulung: boolean;
    private _pensumFachstelle: TSPensumFachstelle;

    constructor(vorname?: string, nachname?: string, geburtsdatum?: moment.Moment, geschlecht?: TSGeschlecht,
                wohnhaftImGleichenHaushalt?: number, kinderabzug?: TSKinderabzug, familienErgaenzendeBetreuung?: boolean,
                mutterspracheDeutsch?: boolean, pensumFachstelle?: TSPensumFachstelle, einschulung?: boolean) {

        super(vorname, nachname, geburtsdatum, geschlecht);
        this._wohnhaftImGleichenHaushalt = wohnhaftImGleichenHaushalt;
        this._kinderabzug = kinderabzug;
        this._familienErgaenzendeBetreuung = familienErgaenzendeBetreuung;
        this._mutterspracheDeutsch = mutterspracheDeutsch;
        this._einschulung = einschulung;
        this._pensumFachstelle = pensumFachstelle;
    }

    get wohnhaftImGleichenHaushalt(): number {
        return this._wohnhaftImGleichenHaushalt;
    }

    set wohnhaftImGleichenHaushalt(value: number) {
        this._wohnhaftImGleichenHaushalt = value;
    }

    get kinderabzug(): TSKinderabzug {
        return this._kinderabzug;
    }

    set kinderabzug(value: TSKinderabzug) {
        this._kinderabzug = value;
    }

    get familienErgaenzendeBetreuung(): boolean {
        return this._familienErgaenzendeBetreuung;
    }

    set familienErgaenzendeBetreuung(value: boolean) {
        this._familienErgaenzendeBetreuung = value;
    }

    get mutterspracheDeutsch(): boolean {
        return this._mutterspracheDeutsch;
    }

    set mutterspracheDeutsch(value: boolean) {
        this._mutterspracheDeutsch = value;
    }

    get pensumFachstelle(): TSPensumFachstelle {
        return this._pensumFachstelle;
    }

    set pensumFachstelle(value: TSPensumFachstelle) {
        this._pensumFachstelle = value;
    }

    get einschulung(): boolean {
        return this._einschulung;
    }

    set einschulung(value: boolean) {
        this._einschulung = value;
    }
}
