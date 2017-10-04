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

package ch.dvbern.ebegu.enums;

/**
 * AntragStatus fuer das DTO.
 * Der Status von einem Antrag ist in der DB anders als auf dem Client. Grund dafür ist, dass es bei manchen DB-AntragStatus verschiedene "substatus"
 * gibt. Auf dem Client wird der "substatus" anstatt der status angezeigt.
 * Beispiel:
 * Der Status PLATZBESTAETIGUNG_WARTEN wird nur auf dem Client angezeigt, wenn noch mindestens eine Platzanfrage
 * ausstehend ist. Auf dem Server bleibt es aber im Status IN_BEARBEITUNG.
 */
public enum AntragStatusDTO {
	IN_BEARBEITUNG_GS,
	FREIGABEQUITTUNG,
	NUR_SCHULAMT,
	FREIGEGEBEN,
	IN_BEARBEITUNG_JA,
	ERSTE_MAHNUNG,
	ERSTE_MAHNUNG_ABGELAUFEN,
	ZWEITE_MAHNUNG,
	ZWEITE_MAHNUNG_ABGELAUFEN,
	GEPRUEFT,
	PLATZBESTAETIGUNG_WARTEN,
	PLATZBESTAETIGUNG_ABGEWIESEN,
	VERFUEGEN,
	VERFUEGT,
	KEIN_ANGEBOT,
	BESCHWERDE_HAENGIG,
	PRUEFUNG_STV,
	IN_BEARBEITUNG_STV,
	GEPRUEFT_STV
}
