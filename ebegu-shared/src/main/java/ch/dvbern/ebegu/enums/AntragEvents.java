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
 * Enum fuer den Status Events vom Gesuch.
 */
public enum AntragEvents {
	FREIGABEQUITTUNG_ERSTELLEN,
	FREIGEBEN,
	ERSTES_OEFFNEN_JA,
	MAHNEN, GEPRUEFT,
	ZUWEISUNG_SCHULAMT,
	VERFUEGUNG_STARTEN,
	VERFUEGEN_OHNE_ANGEBOT,
	VERFUEGEN,
	BESCHWEREN,
	PRUEFEN_STV,
	DOKUMENTE_GEPRUEFT,
	ZURUECK_NUR_SCHULAMT,
	ZURUECK_VERFUEGT,
	ZURUECK_KEIN_ANGEBOT,
	ZURUECK_PRUEFUNG_STV,
	ZURUECK_IN_BEARBEITUNG_STV,
	ZURUECK_GEPRUEFT_STV,
	ERSTES_OEFFNEN_STV,
	GEPRUEFT_STV,
	PRUEFUNG_ABGESCHLOSSEN,
	MAHNUNG_ABGELAUFEN,
	MAHNLAUF_BEENDEN
}
