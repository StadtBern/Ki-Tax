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

export enum TSEbeguVorlageKey {
    VORLAGE_MAHNUNG_1 = <any>'VORLAGE_MAHNUNG_1',
    VORLAGE_MAHNUNG_2 = <any>'VORLAGE_MAHNUNG_2',
    VORLAGE_NICHT_EINTRETENSVERFUEGUNG = <any>'VORLAGE_NICHT_EINTRETENSVERFUEGUNG',
    VORLAGE_INFOSCHREIBEN_MAXIMALTARIF = <any>'VORLAGE_INFOSCHREIBEN_MAXIMALTARIF',
    VORLAGE_VERFUEGUNG_TAGESELTERN_KLEINKINDER = <any>'VORLAGE_VERFUEGUNG_TAGESELTERN_KLEINKINDER',
    VORLAGE_BRIEF_TAGESELTERN_SCHULKINDER = <any>'VORLAGE_BRIEF_TAGESELTERN_SCHULKINDER',
    VORLAGE_FREIGABEQUITTUNG = <any>'VORLAGE_FREIGABEQUITTUNG',
    VORLAGE_FINANZIELLE_SITUATION = <any>'VORLAGE_FINANZIELLE_SITUATION',
    VORLAGE_BEGLEITSCHREIBEN = <any>'VORLAGE_BEGLEITSCHREIBEN',
    VORLAGE_VERFUEGUNG_KITA = <any>'VORLAGE_VERFUEGUNG_KITA',
    VORLAGE_BRIEF_TAGESSTAETTE_SCHULKINDER = <any>'VORLAGE_BRIEF_TAGESSTAETTE_SCHULKINDER'
}

export function getTSEbeguVorlageKeyValues(): Array<TSEbeguVorlageKey> {
    return [
        TSEbeguVorlageKey.VORLAGE_MAHNUNG_1,
        TSEbeguVorlageKey.VORLAGE_MAHNUNG_2,
        TSEbeguVorlageKey.VORLAGE_NICHT_EINTRETENSVERFUEGUNG,
        TSEbeguVorlageKey.VORLAGE_INFOSCHREIBEN_MAXIMALTARIF,
        TSEbeguVorlageKey.VORLAGE_VERFUEGUNG_TAGESELTERN_KLEINKINDER,
        TSEbeguVorlageKey.VORLAGE_BRIEF_TAGESELTERN_SCHULKINDER,
        TSEbeguVorlageKey.VORLAGE_FREIGABEQUITTUNG,
        TSEbeguVorlageKey.VORLAGE_FINANZIELLE_SITUATION,
        TSEbeguVorlageKey.VORLAGE_BEGLEITSCHREIBEN,
        TSEbeguVorlageKey.VORLAGE_VERFUEGUNG_KITA,
        TSEbeguVorlageKey.VORLAGE_BRIEF_TAGESSTAETTE_SCHULKINDER
    ];
}
