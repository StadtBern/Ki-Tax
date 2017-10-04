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

import TSInstitution from './TSInstitution';
import {TSBetreuungsangebotTyp} from './enums/TSBetreuungsangebotTyp';
import {TSDateRange} from './types/TSDateRange';
import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';
import TSAdresse from './TSAdresse';

export default class TSInstitutionStammdaten extends TSAbstractDateRangedEntity {

    private _iban: string;
    private _oeffnungstage: number;
    private _oeffnungsstunden: number;
    private _betreuungsangebotTyp: TSBetreuungsangebotTyp;
    private _institution: TSInstitution;
    private _adresse: TSAdresse;
    private _kontoinhaber: string;
    private _adresseKontoinhaber: TSAdresse;


    constructor(iban?: string, oeffnungstage?: number, oeffnungsstunden?: number, betreuungsangebotTyp?: TSBetreuungsangebotTyp,
                institution?: TSInstitution, adresse?: TSAdresse, gueltigkeit?: TSDateRange, kontoinhaber?: string, adresseKontoinhaber?: TSAdresse) {
        super(gueltigkeit);
        this._iban = iban;
        this._oeffnungstage = oeffnungstage;
        this._oeffnungsstunden = oeffnungsstunden;
        this._betreuungsangebotTyp = betreuungsangebotTyp;
        this._institution = institution;
        this._adresse = adresse;
        this._kontoinhaber = kontoinhaber;
        this._adresseKontoinhaber = adresseKontoinhaber;
    }


    public get iban(): string {
        return this._iban;
    }

    public set iban(value: string) {
        this._iban = value;
    }

    public get oeffnungstage(): number {
        return this._oeffnungstage;
    }

    public set oeffnungstage(value: number) {
        this._oeffnungstage = value;
    }

    public get oeffnungsstunden(): number {
        return this._oeffnungsstunden;
    }

    public set oeffnungsstunden(value: number) {
        this._oeffnungsstunden = value;
    }

    public get betreuungsangebotTyp(): TSBetreuungsangebotTyp {
        return this._betreuungsangebotTyp;
    }

    public set betreuungsangebotTyp(value: TSBetreuungsangebotTyp) {
        this._betreuungsangebotTyp = value;
    }

    public get institution(): TSInstitution {
        return this._institution;
    }

    public set institution(value: TSInstitution) {
        this._institution = value;
    }

    public get adresse(): TSAdresse {
        return this._adresse;
    }

    public set adresse(value: TSAdresse) {
        this._adresse = value;
    }

    public get kontoinhaber(): string {
        return this._kontoinhaber;
    }

    public set kontoinhaber(value: string) {
        this._kontoinhaber = value;
    }

    public get adresseKontoinhaber(): TSAdresse {
        return this._adresseKontoinhaber;
    }

    public set adresseKontoinhaber(value: TSAdresse) {
        this._adresseKontoinhaber = value;
    }
}
