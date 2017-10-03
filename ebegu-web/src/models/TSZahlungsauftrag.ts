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
import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';
import {TSDateRange} from './types/TSDateRange';
import TSZahlung from './TSZahlung';
import {TSZahlungsauftragsstatus} from './enums/TSZahlungsauftragstatus';

export default class TSZahlungsauftrag extends TSAbstractDateRangedEntity {


    private _datumGeneriert: moment.Moment;
    private _datumFaellig: moment.Moment;
    private _status: TSZahlungsauftragsstatus;
    private _beschrieb: string;
    private _betragTotalAuftrag: number;
    private _zahlungen: Array<TSZahlung>;


    constructor(gueltigkeit?: TSDateRange, datumGeneriert?: moment.Moment, datumFaellig?: moment.Moment,
                status?: TSZahlungsauftragsstatus, beschrieb?: string, betragTotalAuftrag?: number, zahlungen?: Array<TSZahlung>) {
        super(gueltigkeit);
        this._datumGeneriert = datumGeneriert;
        this._datumFaellig = datumFaellig;
        this._status = status;
        this._beschrieb = beschrieb;
        this._betragTotalAuftrag = betragTotalAuftrag;
        this._zahlungen = zahlungen;
    }

    get datumGeneriert(): moment.Moment {
        return this._datumGeneriert;
    }

    set datumGeneriert(value: moment.Moment) {
        this._datumGeneriert = value;
    }

    get datumFaellig(): moment.Moment {
        return this._datumFaellig;
    }

    set datumFaellig(value: moment.Moment) {
        this._datumFaellig = value;
    }

    get beschrieb(): string {
        return this._beschrieb;
    }

    set beschrieb(value: string) {
        this._beschrieb = value;
    }

    get betragTotalAuftrag(): number {
        return this._betragTotalAuftrag;
    }

    set betragTotalAuftrag(value: number) {
        this._betragTotalAuftrag = value;
    }

    get zahlungen(): Array<TSZahlung> {
        return this._zahlungen;
    }

    set zahlungen(value: Array<TSZahlung>) {
        this._zahlungen = value;
    }

    get status(): TSZahlungsauftragsstatus {
        return this._status;
    }

    set status(value: TSZahlungsauftragsstatus) {
        this._status = value;
    }
}


