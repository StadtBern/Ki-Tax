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

package ch.dvbern.ebegu.api.enums;

public enum JaxExternalAntragstatus {

	//TODO (team) Werden von Scolaris alle Status benötigt?

	IN_BEARBEITUNG_GS,
	FREIGABEQUITTUNG,   // = GS hat Freigabequittung gedruckt, bzw. den Antrag freigegeben (auch wenn keine Freigabequittung notwendig ist)
	NUR_SCHULAMT,
	FREIGEGEBEN,        // Freigabequittung im Jugendamt eingelesen ODER keine Quittung notwendig
	IN_BEARBEITUNG_JA,
	ERSTE_MAHNUNG,
	ERSTE_MAHNUNG_ABGELAUFEN,
	ZWEITE_MAHNUNG,
	ZWEITE_MAHNUNG_ABGELAUFEN,
	GEPRUEFT,
	VERFUEGEN,
	VERFUEGT,
	KEIN_ANGEBOT,
	BESCHWERDE_HAENGIG,
	PRUEFUNG_STV,
	IN_BEARBEITUNG_STV,
	GEPRUEFT_STV;
}
