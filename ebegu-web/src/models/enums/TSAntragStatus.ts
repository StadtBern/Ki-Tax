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

import {TSRole} from './TSRole';

export enum TSAntragStatus {
    IN_BEARBEITUNG_GS = <any> 'IN_BEARBEITUNG_GS',
    FREIGABEQUITTUNG = <any> 'FREIGABEQUITTUNG',
    NUR_SCHULAMT = <any> 'NUR_SCHULAMT',
    FREIGEGEBEN = <any> 'FREIGEGEBEN',
    ERSTE_MAHNUNG = <any> 'ERSTE_MAHNUNG',
    ERSTE_MAHNUNG_ABGELAUFEN = <any> 'ERSTE_MAHNUNG_ABGELAUFEN',
    ZWEITE_MAHNUNG = <any> 'ZWEITE_MAHNUNG',
    ZWEITE_MAHNUNG_ABGELAUFEN = <any> 'ZWEITE_MAHNUNG_ABGELAUFEN',
    IN_BEARBEITUNG_JA = <any> 'IN_BEARBEITUNG_JA',
    GEPRUEFT = <any> 'GEPRUEFT',
    PLATZBESTAETIGUNG_ABGEWIESEN = <any> 'PLATZBESTAETIGUNG_ABGEWIESEN',
    PLATZBESTAETIGUNG_WARTEN = <any> 'PLATZBESTAETIGUNG_WARTEN',
    VERFUEGEN = <any> 'VERFUEGEN',
    VERFUEGT = <any> 'VERFUEGT',
    KEIN_ANGEBOT = <any> 'KEIN_ANGEBOT',
    BESCHWERDE_HAENGIG = <any> 'BESCHWERDE_HAENGIG',
    PRUEFUNG_STV = <any> 'PRUEFUNG_STV',
    IN_BEARBEITUNG_STV = <any> 'IN_BEARBEITUNG_STV',
    GEPRUEFT_STV = <any> 'GEPRUEFT_STV'
}

export const IN_BEARBEITUNG_BASE_NAME = 'IN_BEARBEITUNG';

export function getTSAntragStatusValues(): Array<TSAntragStatus> {
    return [
        TSAntragStatus.IN_BEARBEITUNG_GS,
        TSAntragStatus.FREIGABEQUITTUNG,
        TSAntragStatus.NUR_SCHULAMT,
        TSAntragStatus.FREIGEGEBEN,
        TSAntragStatus.ERSTE_MAHNUNG,
        TSAntragStatus.ERSTE_MAHNUNG_ABGELAUFEN,
        TSAntragStatus.ZWEITE_MAHNUNG,
        TSAntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN,
        TSAntragStatus.IN_BEARBEITUNG_JA,
        TSAntragStatus.GEPRUEFT,
        TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN,
        TSAntragStatus.PLATZBESTAETIGUNG_WARTEN,
        TSAntragStatus.VERFUEGEN,
        TSAntragStatus.VERFUEGT,
        TSAntragStatus.KEIN_ANGEBOT,
        TSAntragStatus.BESCHWERDE_HAENGIG,
        TSAntragStatus.PRUEFUNG_STV,
        TSAntragStatus.IN_BEARBEITUNG_STV,
        TSAntragStatus.GEPRUEFT_STV
    ];
}

/**
 * Alle Status die eine gewisse Rolle sehen darf
 */
export function getTSAntragStatusValuesByRole(userrole: TSRole): Array<TSAntragStatus> {
    switch (userrole) {
        case TSRole.STEUERAMT:
            return [
                TSAntragStatus.PRUEFUNG_STV,
                TSAntragStatus.IN_BEARBEITUNG_STV
            ];
        case TSRole.SCHULAMT:
        case TSRole.ADMINISTRATOR_SCHULAMT:
        case TSRole.SACHBEARBEITER_JA:
        case TSRole.ADMIN:
        case TSRole.REVISOR:
        case TSRole.JURIST:
            return getTSAntragStatusValues().filter(element => (element !== TSAntragStatus.IN_BEARBEITUNG_GS
                && element !== TSAntragStatus.FREIGABEQUITTUNG));
        case TSRole.SACHBEARBEITER_INSTITUTION:
        case TSRole.SACHBEARBEITER_TRAEGERSCHAFT:
            return getTSAntragStatusValues().filter(element => (element !== TSAntragStatus.PRUEFUNG_STV
                && element !== TSAntragStatus.IN_BEARBEITUNG_STV && element !== TSAntragStatus.GEPRUEFT_STV));
        default:
            return getTSAntragStatusValues();
    }
}

/**
 * Gibt alle Werte zurueck ausser VERFUEGT und KEIN_ANGEBOT.
 * Diese Werte sind die, die bei der Pendenzenliste notwendig sind
 * @returns {TSAntragStatus[]}
 */
export function getTSAntragStatusPendenzValues(userrole: TSRole): Array<TSAntragStatus> {
    let allVisibleValuesByRole = getTSAntragStatusValuesByRole(userrole);
    switch (userrole) {
        case TSRole.SACHBEARBEITER_JA:
        case TSRole.ADMIN:
        case TSRole.REVISOR:
        case TSRole.JURIST:
            return allVisibleValuesByRole.filter(element => (element !== TSAntragStatus.VERFUEGT
                && element !== TSAntragStatus.KEIN_ANGEBOT && element !== TSAntragStatus.NUR_SCHULAMT
                && element !== TSAntragStatus.IN_BEARBEITUNG_STV && element !== TSAntragStatus.PRUEFUNG_STV));
        case TSRole.SCHULAMT:
        case TSRole.ADMINISTRATOR_SCHULAMT:
            return allVisibleValuesByRole.filter(element => (element !== TSAntragStatus.VERFUEGT
                && element !== TSAntragStatus.KEIN_ANGEBOT && element !== TSAntragStatus.NUR_SCHULAMT
                && element !== TSAntragStatus.VERFUEGEN && element !== TSAntragStatus.IN_BEARBEITUNG_STV
                && element !== TSAntragStatus.PRUEFUNG_STV));
        default:
            return allVisibleValuesByRole.filter(element => (element !== TSAntragStatus.VERFUEGT
                && element !== TSAntragStatus.KEIN_ANGEBOT && element !== TSAntragStatus.NUR_SCHULAMT));
    }
}

export function isAtLeastFreigegeben(status: TSAntragStatus): boolean {
    let validStates: Array<TSAntragStatus> = [
        TSAntragStatus.NUR_SCHULAMT,
        TSAntragStatus.FREIGEGEBEN,
        TSAntragStatus.ERSTE_MAHNUNG,
        TSAntragStatus.ERSTE_MAHNUNG_ABGELAUFEN,
        TSAntragStatus.ZWEITE_MAHNUNG,
        TSAntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN,
        TSAntragStatus.IN_BEARBEITUNG_JA,
        TSAntragStatus.GEPRUEFT,
        TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN,
        TSAntragStatus.PLATZBESTAETIGUNG_WARTEN,
        TSAntragStatus.VERFUEGEN,
        TSAntragStatus.VERFUEGT,
        TSAntragStatus.KEIN_ANGEBOT,
        TSAntragStatus.BESCHWERDE_HAENGIG,
        TSAntragStatus.PRUEFUNG_STV,
        TSAntragStatus.IN_BEARBEITUNG_STV,
        TSAntragStatus.GEPRUEFT_STV];
    return validStates.indexOf(status) !== -1;
}

export function isAtLeastFreigegebenOrFreigabequittung(status: TSAntragStatus): boolean {
    return isAtLeastFreigegeben(status) || status === TSAntragStatus.FREIGABEQUITTUNG;
}

export function isAnyStatusOfVerfuegt(status: TSAntragStatus): boolean {
    return status === TSAntragStatus.NUR_SCHULAMT ||
        status === TSAntragStatus.VERFUEGT || status === TSAntragStatus.BESCHWERDE_HAENGIG || status === TSAntragStatus.PRUEFUNG_STV
        || status === TSAntragStatus.IN_BEARBEITUNG_STV || status === TSAntragStatus.GEPRUEFT_STV || status === TSAntragStatus.KEIN_ANGEBOT;
}

export function isAnyStatusOfVerfuegtButSchulamt(status: TSAntragStatus): boolean {
    return status === TSAntragStatus.VERFUEGT || status === TSAntragStatus.BESCHWERDE_HAENGIG || status === TSAntragStatus.PRUEFUNG_STV
        || status === TSAntragStatus.IN_BEARBEITUNG_STV || status === TSAntragStatus.GEPRUEFT_STV || status === TSAntragStatus.KEIN_ANGEBOT;
}

export function isVerfuegtOrSTV(status: TSAntragStatus): boolean {
    return status === TSAntragStatus.VERFUEGT || status === TSAntragStatus.PRUEFUNG_STV
        || status === TSAntragStatus.IN_BEARBEITUNG_STV || status === TSAntragStatus.GEPRUEFT_STV
        || status === TSAntragStatus.KEIN_ANGEBOT;
}

/**
 * Returns true when the status of the Gesuch is VERFUEGEN or VERFUEGT or NUR_SCHULAMT
 * @returns {boolean}
 */
export function isStatusVerfuegenVerfuegt(status: TSAntragStatus): boolean {
    return isAnyStatusOfVerfuegt(status) || status === TSAntragStatus.VERFUEGEN;
}

export function isAnyStatusOfMahnung(status: TSAntragStatus): boolean {
    return status === TSAntragStatus.ERSTE_MAHNUNG || status === TSAntragStatus.ERSTE_MAHNUNG_ABGELAUFEN
        || status === TSAntragStatus.ZWEITE_MAHNUNG || status === TSAntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN;
}
