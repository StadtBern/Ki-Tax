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

import {TSBetreuungsangebotTyp} from './enums/TSBetreuungsangebotTyp';
import {TSAntragTyp} from './enums/TSAntragTyp';
import {TSAntragStatus} from './enums/TSAntragStatus';
import {TSEingangsart} from './enums/TSEingangsart';
import * as moment from 'moment';
import {TSGesuchBetreuungenStatus} from './enums/TSGesuchBetreuungenStatus';
import TSAbstractAntragDTO from './TSAbstractAntragDTO';

export default class TSAntragDTO extends TSAbstractAntragDTO {

    private _antragId: string;
    private _antragTyp: TSAntragTyp;
    private _eingangsart: TSEingangsart;
    private _eingangsdatum: moment.Moment;
    private _eingangsdatumSTV: moment.Moment;
    private _aenderungsdatum: moment.Moment;
    private _verantwortlicher: string;
    private _verantwortlicherSCH: string;
    private _besitzerUsername: string;
    private _angebote: Array<TSBetreuungsangebotTyp>;
    private _institutionen: Array<string>;
    private _kinder: Array<string>;
    private _status: TSAntragStatus;
    private _gesuchsperiodeGueltigAb: moment.Moment;
    private _gesuchsperiodeGueltigBis: moment.Moment;
    private _verfuegt: boolean;
    private _beschwerdeHaengig: boolean;
    private _laufnummer: number;
    private _gesuchBetreuungenStatus: TSGesuchBetreuungenStatus;
    private _dokumenteHochgeladen: boolean;

    constructor(antragId?: string, fallNummer?: number, familienName?: string, antragTyp?: TSAntragTyp,
                eingangsdatum?: moment.Moment, eingangsdatumSTV?: moment.Moment, aenderungsdatum?: moment.Moment, angebote?: Array<TSBetreuungsangebotTyp>,
                institutionen?: Array<string>, verantwortlicher?: string, verantwortlicherSCH?: string, status?: TSAntragStatus,
                gesuchsperiodeGueltigAb?: moment.Moment, gesuchsperiodeGueltigBis?: moment.Moment,
                verfuegt?: boolean, laufnummer?: number, besitzerUsername?: string, eingangsart?: TSEingangsart, beschwerdeHaengig?: boolean,
                kinder?: Array<string>, gesuchBetreuungenStatus?: TSGesuchBetreuungenStatus, dokumenteHochgeladen?: boolean) {

        super(fallNummer, familienName);
        this._antragId = antragId;
        this._antragTyp = antragTyp;
        this._eingangsdatum = eingangsdatum;
        this._eingangsdatumSTV = eingangsdatumSTV;
        this._aenderungsdatum = aenderungsdatum;
        this._angebote = angebote;
        this._institutionen = institutionen;
        this._verantwortlicher = verantwortlicher;
        this._verantwortlicherSCH = verantwortlicherSCH;
        this._status = status;
        this._gesuchsperiodeGueltigAb = gesuchsperiodeGueltigAb;
        this._gesuchsperiodeGueltigBis = gesuchsperiodeGueltigBis;
        this._verfuegt = verfuegt;
        this._laufnummer = laufnummer;
        this._besitzerUsername = besitzerUsername;
        this._eingangsart = eingangsart;
        this._beschwerdeHaengig = beschwerdeHaengig;
        this._kinder = kinder;
        this._gesuchBetreuungenStatus = gesuchBetreuungenStatus;
        this._dokumenteHochgeladen = dokumenteHochgeladen;
    }

    get antragId(): string {
        return this._antragId;
    }

    set antragId(value: string) {
        this._antragId = value;
    }

    get antragTyp(): TSAntragTyp {
        return this._antragTyp;
    }

    set antragTyp(value: TSAntragTyp) {
        this._antragTyp = value;
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

    get aenderungsdatum(): moment.Moment {
        return this._aenderungsdatum;
    }

    set aenderungsdatum(value: moment.Moment) {
        this._aenderungsdatum = value;
    }

    get angebote(): Array<TSBetreuungsangebotTyp> {
        return this._angebote;
    }

    set angebote(value: Array<TSBetreuungsangebotTyp>) {
        this._angebote = value;
    }

    get institutionen(): Array<string> {
        return this._institutionen;
    }

    set institutionen(value: Array<string>) {
        this._institutionen = value;
    }

    get verantwortlicher(): string {
        return this._verantwortlicher;
    }

    set verantwortlicher(value: string) {
        this._verantwortlicher = value;
    }

    public get verantwortlicherSCH(): string {
        return this._verantwortlicherSCH;
    }

    public set verantwortlicherSCH(value: string) {
        this._verantwortlicherSCH = value;
    }

    get status(): TSAntragStatus {
        return this._status;
    }

    set status(value: TSAntragStatus) {
        this._status = value;
    }

    get gesuchsperiodeGueltigAb(): moment.Moment {
        return this._gesuchsperiodeGueltigAb;
    }

    set gesuchsperiodeGueltigAb(value: moment.Moment) {
        this._gesuchsperiodeGueltigAb = value;
    }

    get gesuchsperiodeGueltigBis(): moment.Moment {
        return this._gesuchsperiodeGueltigBis;
    }

    set gesuchsperiodeGueltigBis(value: moment.Moment) {
        this._gesuchsperiodeGueltigBis = value;
    }

    get verfuegt(): boolean {
        return this._verfuegt;
    }

    set verfuegt(value: boolean) {
        this._verfuegt = value;
    }

    get laufnummer(): number {
        return this._laufnummer;
    }

    set laufnummer(value: number) {
        this._laufnummer = value;
    }

    get gesuchsperiodeString(): string {
        if (this._gesuchsperiodeGueltigAb && this._gesuchsperiodeGueltigBis) {
            return this._gesuchsperiodeGueltigAb.year() + '/'
                + (this._gesuchsperiodeGueltigBis.year() - 2000);
        }
        return undefined;
    }

    get eingangsart(): TSEingangsart {
        return this._eingangsart;
    }

    set eingangsart(value: TSEingangsart) {
        this._eingangsart = value;
    }

    get besitzerUsername(): string {
        return this._besitzerUsername;
    }

    set besitzerUsername(value: string) {
        this._besitzerUsername = value;
    }

    public hasBesitzer(): boolean {
        return this._besitzerUsername !== undefined && this.besitzerUsername !== null;
    }

    get beschwerdeHaengig(): boolean {
        return this._beschwerdeHaengig;
    }

    set beschwerdeHaengig(value: boolean) {
        this._beschwerdeHaengig = value;
    }

    get kinder(): Array<string> {
        return this._kinder;
    }

    set kinder(value: Array<string>) {
        this._kinder = value;
    }

    get dokumenteHochgeladen(): boolean {
        return this._dokumenteHochgeladen;
    }

    set dokumenteHochgeladen(value: boolean) {
        this._dokumenteHochgeladen = value;
    }

    public canBeFreigegeben(): boolean {
        return this.status === TSAntragStatus.FREIGABEQUITTUNG;
    }

    public hasAnySchulamtAngebot(): boolean {
        for (let angebot of this.angebote) {
            if (TSBetreuungsangebotTyp.TAGESSCHULE === angebot || TSBetreuungsangebotTyp.FERIENINSEL === angebot) {
                return true;
            }
        }
        return false;
    }

    public hasAnyJugendamtAngebot(): boolean {
        for (let angebot of this.angebote) {
            if (TSBetreuungsangebotTyp.TAGESSCHULE !== angebot && TSBetreuungsangebotTyp.FERIENINSEL !== angebot) {
                return true;
            }
        }
        return false;
    }

    public get gesuchBetreuungenStatus(): TSGesuchBetreuungenStatus {
        return this._gesuchBetreuungenStatus;
    }

    public set gesuchBetreuungenStatus(value: TSGesuchBetreuungenStatus) {
        this._gesuchBetreuungenStatus = value;
    }
}
