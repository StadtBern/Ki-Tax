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

package ch.dvbern.ebegu.util;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.enums.UserRole;

/**
 * Diese Klasse enthaelt Methoden, um den AntragStatus von DB in DTO umzuwandeln.
 */
@SuppressWarnings("OverlyComplexMethod")
public class AntragStatusConverterUtil {

	private AntragStatusConverterUtil() {
		// Util-Methode soll nicht instanziert werden
	}

	/**
	 * In dieser Methode wird der Status umgewandelt. Alle relevanten Daten werden geprueft und dadurch den entsprechenden
	 * AntragStatusDTO zurueckgeliefert
	 *
	 * @param status Der AntragStatus vom Entity
	 * @return Der AntragStatusDTO, der zum Client geschickt wird
	 */
	public static AntragStatusDTO convertStatusToDTO(Gesuch antrag, AntragStatus status) {
		switch (status) {
		case IN_BEARBEITUNG_GS:
			return AntragStatusDTO.IN_BEARBEITUNG_GS;
		case FREIGABEQUITTUNG:
			return AntragStatusDTO.FREIGABEQUITTUNG;
		case NUR_SCHULAMT:
			return AntragStatusDTO.NUR_SCHULAMT;
		case FREIGEGEBEN:
			return AntragStatusDTO.FREIGEGEBEN;
		case IN_BEARBEITUNG_JA:
			return AntragStatusDTO.IN_BEARBEITUNG_JA;
		case ERSTE_MAHNUNG:
			return AntragStatusDTO.ERSTE_MAHNUNG;
		case ERSTE_MAHNUNG_ABGELAUFEN:
			return AntragStatusDTO.ERSTE_MAHNUNG_ABGELAUFEN;
		case ZWEITE_MAHNUNG:
			return AntragStatusDTO.ZWEITE_MAHNUNG;
		case ZWEITE_MAHNUNG_ABGELAUFEN:
			return AntragStatusDTO.ZWEITE_MAHNUNG_ABGELAUFEN;
		case GEPRUEFT:
			return convertGeprueftStatusToDTO(antrag);
		case VERFUEGEN:
			return AntragStatusDTO.VERFUEGEN;
		case VERFUEGT:
			return AntragStatusDTO.VERFUEGT;
		case KEIN_ANGEBOT:
			return AntragStatusDTO.KEIN_ANGEBOT;
		case BESCHWERDE_HAENGIG:
			return AntragStatusDTO.BESCHWERDE_HAENGIG;
		case PRUEFUNG_STV:
			return AntragStatusDTO.PRUEFUNG_STV;
		case IN_BEARBEITUNG_STV:
			return AntragStatusDTO.IN_BEARBEITUNG_STV;
		case GEPRUEFT_STV:
			return AntragStatusDTO.GEPRUEFT_STV;
		default:
			return null;
		}
	}

	/**
	 * Wenn alle Betreuungen bestaetigt sind, der Status ist GEPRUEFT, wenn eine Betreuung am Warten ist, ist der Status PLATZBESTAETIGUNG_WARTEN
	 * und wenn eine Betreuung abgewiesen wurde (Prioritaet A) ist der Status PLATZBESTAETIGUNG_ABGEWIESEN.
	 * Beim Fehler oder Zweifelnfall ist der Status einfach GEPRUEFT
	 */
	@Nonnull
	private static AntragStatusDTO convertGeprueftStatusToDTO(Gesuch antrag) {
		switch (antrag.getGesuchBetreuungenStatus()) {
		case WARTEN:
			return AntragStatusDTO.PLATZBESTAETIGUNG_WARTEN;
		case ABGEWIESEN:
			return AntragStatusDTO.PLATZBESTAETIGUNG_ABGEWIESEN;
		default:
			return AntragStatusDTO.GEPRUEFT;
		}
	}

	/**
	 * Konvertieren des DTOStatus in Entity-Status. Die Werte sollen in die Serverstatus konvertiert werden
	 *
	 * @param statusDTO Der AntragStatusDTO vom Client
	 * @return Der AntragStatus fuer das Entity
	 */
	public static AntragStatus convertStatusToEntity(AntragStatusDTO statusDTO) {
		switch (statusDTO) {
		case IN_BEARBEITUNG_GS:
			return AntragStatus.IN_BEARBEITUNG_GS;
		case FREIGABEQUITTUNG:
			return AntragStatus.FREIGABEQUITTUNG;
		case NUR_SCHULAMT:
			return AntragStatus.NUR_SCHULAMT;
		case FREIGEGEBEN:
			return AntragStatus.FREIGEGEBEN;
		case IN_BEARBEITUNG_JA:
			return AntragStatus.IN_BEARBEITUNG_JA;
		case ERSTE_MAHNUNG:
			return AntragStatus.ERSTE_MAHNUNG;
		case ERSTE_MAHNUNG_ABGELAUFEN:
			return AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN;
		case ZWEITE_MAHNUNG:
			return AntragStatus.ZWEITE_MAHNUNG;
		case ZWEITE_MAHNUNG_ABGELAUFEN:
			return AntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN;
		case PLATZBESTAETIGUNG_ABGEWIESEN:
		case PLATZBESTAETIGUNG_WARTEN:
		case GEPRUEFT:
			return AntragStatus.GEPRUEFT;
		case VERFUEGEN:
			return AntragStatus.VERFUEGEN;
		case VERFUEGT:
			return AntragStatus.VERFUEGT;
		case KEIN_ANGEBOT:
			return AntragStatus.KEIN_ANGEBOT;
		case BESCHWERDE_HAENGIG:
			return AntragStatus.BESCHWERDE_HAENGIG;
		case PRUEFUNG_STV:
			return AntragStatus.PRUEFUNG_STV;
		case IN_BEARBEITUNG_STV:
			return AntragStatus.IN_BEARBEITUNG_STV;
		case GEPRUEFT_STV:
			return AntragStatus.GEPRUEFT_STV;
		default:
			return null;
		}
	}

	public static Collection<AntragStatus> convertStatusToEntityForRole(AntragStatusDTO statusDTO, UserRole userrole) {
		Collection<AntragStatus> tmp = new ArrayList<>();
		switch (userrole) {
		case GESUCHSTELLER:
		case SACHBEARBEITER_INSTITUTION:
		case SACHBEARBEITER_TRAEGERSCHAFT: {
			tmp.add(convertStatusToEntity(statusDTO));
			if (statusDTO == AntragStatusDTO.VERFUEGT) {
				tmp.add(AntragStatus.PRUEFUNG_STV);
				tmp.add(AntragStatus.IN_BEARBEITUNG_STV);
				tmp.add(AntragStatus.GEPRUEFT_STV);
			}
			return tmp;
		}
		default: {
			tmp.add(convertStatusToEntity(statusDTO));
			return tmp;
		}

		}
	}
}
