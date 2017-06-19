package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.UserRole;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Diese Klasse enthaelt Methoden, um den AntragStatus von DB in DTO umzuwandeln.
 */
public class AntragStatusConverterUtil {

	private AntragStatusConverterUtil() {
		// Util-Methode soll nicht instanziert werden
	}

	/**
	 * In dieser Methode wird der Status umgewandelt. Alle relevanten Daten werden geprueft und dadurch den entsprechenden
	 * AntragStatusDTO zurueckgeliefert
	 *
	 * @param antrag
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
			case NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN:
				return AntragStatusDTO.NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN;
			case FREIGEGEBEN:
				return AntragStatusDTO.FREIGEGEBEN;
			case IN_BEARBEITUNG_JA:
				return AntragStatusDTO.IN_BEARBEITUNG_JA;
			case ERSTE_MAHNUNG:
				return AntragStatusDTO.ERSTE_MAHNUNG;
			case ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN:
				return AntragStatusDTO.ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN;
			case ERSTE_MAHNUNG_ABGELAUFEN:
				return AntragStatusDTO.ERSTE_MAHNUNG_ABGELAUFEN;
			case ZWEITE_MAHNUNG:
				return AntragStatusDTO.ZWEITE_MAHNUNG;
			case ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN:
				return AntragStatusDTO.ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN;
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
		final List<Betreuung> allBetreuungenFromGesuch = antrag.extractAllBetreuungen();
		AntragStatusDTO newAntragStatus = AntragStatusDTO.GEPRUEFT; // by default alle plaetze sind bestaetigt
		for (final Betreuung betreuung : allBetreuungenFromGesuch) {
			if (Betreuungsstatus.WARTEN.equals(betreuung.getBetreuungsstatus())) {
				return AntragStatusDTO.PLATZBESTAETIGUNG_WARTEN;
			}
			else if (Betreuungsstatus.ABGEWIESEN.equals(betreuung.getBetreuungsstatus())) {
				newAntragStatus = AntragStatusDTO.PLATZBESTAETIGUNG_ABGEWIESEN;
				break;
			}
		}
		return newAntragStatus;
	}

	/**
	 * Konvertieren des DTOStatus in Entity-Status. Die Werte sollen in die Serverstatus konvertiert werden
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
			case NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN:
				return AntragStatus.NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN;
			case FREIGEGEBEN:
				return AntragStatus.FREIGEGEBEN;
			case IN_BEARBEITUNG_JA:
				return AntragStatus.IN_BEARBEITUNG_JA;
			case ERSTE_MAHNUNG:
				return AntragStatus.ERSTE_MAHNUNG;
			case ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN:
				return AntragStatus.ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN;
			case ERSTE_MAHNUNG_ABGELAUFEN:
				return AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN;
			case ZWEITE_MAHNUNG:
				return AntragStatus.ZWEITE_MAHNUNG;
			case ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN:
				return AntragStatus.ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN;
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
