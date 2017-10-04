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
import TSKind from './TSKind';
import TSBetreuung from './TSBetreuung';
import {TSFachstelle} from './TSFachstelle';
import {TSPensumFachstelle} from './TSPensumFachstelle';

export default class TSKindContainer extends TSAbstractEntity {

    private _kindGS: TSKind;
    private _kindJA: TSKind;
    private _betreuungen: Array<TSBetreuung>;
    private _kindNummer: number;
    private _nextNumberBetreuung: number;
    private _kindMutiert: boolean;

    constructor(kindGS?: TSKind, kindJA?: TSKind, betreuungen?: Array<TSBetreuung>, kindNummer?: number,
                nextNumberBetreuung?: number, kindMutiert?: boolean) {
        super();
        this._kindGS = kindGS;
        this._kindJA = kindJA;
        this._betreuungen = betreuungen ? betreuungen : [];
        this._kindNummer = kindNummer;
        this._nextNumberBetreuung = nextNumberBetreuung;
        this._kindMutiert = kindMutiert;
    }


    get kindGS(): TSKind {
        return this._kindGS;
    }

    set kindGS(value: TSKind) {
        this._kindGS = value;
    }

    get kindJA(): TSKind {
        return this._kindJA;
    }

    set kindJA(value: TSKind) {
        this._kindJA = value;
    }

    get betreuungen(): Array<TSBetreuung> {
        return this._betreuungen;
    }

    set betreuungen(value: Array<TSBetreuung>) {
        this._betreuungen = value;
    }

    get kindNummer(): number {
        return this._kindNummer;
    }

    set kindNummer(value: number) {
        this._kindNummer = value;
    }

    get nextNumberBetreuung(): number {
        return this._nextNumberBetreuung;
    }

    set nextNumberBetreuung(value: number) {
        this._nextNumberBetreuung = value;
    }

    get kindMutiert(): boolean {
        return this._kindMutiert;
    }

    set kindMutiert(value: boolean) {
        this._kindMutiert = value;
    }

    public initBetreuungList(): void {
        if (!this.betreuungen) {
            this.betreuungen = [];
        }
    }

    public hasPensumFachstelle(): boolean {
        return this.kindJA !== null && this.kindJA !== undefined
            && this.kindJA.pensumFachstelle !== null && this.kindJA.pensumFachstelle !== undefined;
    }

    public extractFachstelle(): TSFachstelle {
        if (this.hasPensumFachstelle()) {
            return this.kindJA.pensumFachstelle.fachstelle;
        }
        return undefined;
    }

    public extractPensumFachstelle(): TSPensumFachstelle {
        if (this.hasPensumFachstelle()) {
            return this.kindJA.pensumFachstelle;
        }
        return undefined;
    }
}
