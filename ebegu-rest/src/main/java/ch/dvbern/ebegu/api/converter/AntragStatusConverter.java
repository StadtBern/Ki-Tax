package ch.dvbern.ebegu.api.converter;

import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragStatusDTO;

/**
 * Diese Klasse enthaelt Methoden, um den AntragStatus von DB in DTO umzuwandeln.
 */
public class AntragStatusConverter {

	/**
	 * In dieser Methode wird der Status umgewandelt. Alle relevanten Daten werden geprueft und dadurch den entsprechenden
	 * AntragStatusDTO zurueckgeliefert
	 * @param status Der AntragStatus vom Entity
	 * @return Der AntragStatusDTO, der zum Client geschickt wird
	 */
	public static AntragStatusDTO convertStatusToDTO(AntragStatus status) {
		switch (status) {
			case IN_BEARBEITUNG_GS: return AntragStatusDTO.IN_BEARBEITUNG_GS;
			case FREIGABEQUITTUNG: return AntragStatusDTO.FREIGABEQUITTUNG;
			case NUR_SCHULAMT: return AntragStatusDTO.NUR_SCHULAMT;
			case FREIGEGEBEN: return AntragStatusDTO.FREIGEGEBEN;
			case IN_BEARBEITUNG_JA: return AntragStatusDTO.IN_BEARBEITUNG_JA;
			case ZURUECKGEWIESEN: return AntragStatusDTO.ZURUECKGEWIESEN;
			case ERSTE_MAHNUNG: return AntragStatusDTO.ERSTE_MAHNUNG;
			case ERSTE_MAHNUNG_ABGELAUFEN: return AntragStatusDTO.ERSTE_MAHNUNG_ABGELAUFEN;
			case ZWEITE_MAHNUNG: return AntragStatusDTO.ZWEITE_MAHNUNG;
			case ZWEITE_MAHNUNG_ABGELAUFEN: return AntragStatusDTO.ZWEITE_MAHNUNG_ABGELAUFEN;
			case GEPRUEFT: return AntragStatusDTO.GEPRUEFT;
			case VERFUEGEN: return AntragStatusDTO.VERFUEGEN;
			case VERFUEGT: return AntragStatusDTO.VERFUEGT;
			default: return null;
		}
	}

	/**
	 * Konvertieren des DTOStatus in Entity-Status. Die Werte sollen eins-zu-eins konvertiert werden
	 * @param statusDTO Der AntragStatusDTO vom Client
	 * @return Der AntragStatus fuer das Entity
	 */
	public static AntragStatus convertStatusToEntity(AntragStatusDTO statusDTO) {
		switch (statusDTO) {
			case IN_BEARBEITUNG_GS: return AntragStatus.IN_BEARBEITUNG_GS;
			case FREIGABEQUITTUNG: return AntragStatus.FREIGABEQUITTUNG;
			case NUR_SCHULAMT: return AntragStatus.NUR_SCHULAMT;
			case FREIGEGEBEN: return AntragStatus.FREIGEGEBEN;
			case IN_BEARBEITUNG_JA: return AntragStatus.IN_BEARBEITUNG_JA;
			case ZURUECKGEWIESEN: return AntragStatus.ZURUECKGEWIESEN;
			case ERSTE_MAHNUNG: return AntragStatus.ERSTE_MAHNUNG;
			case ERSTE_MAHNUNG_ABGELAUFEN: return AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN;
			case ZWEITE_MAHNUNG: return AntragStatus.ZWEITE_MAHNUNG;
			case ZWEITE_MAHNUNG_ABGELAUFEN: return AntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN;
			case GEPRUEFT: return AntragStatus.GEPRUEFT;
			case VERFUEGEN: return AntragStatus.VERFUEGEN;
			case VERFUEGT: return AntragStatus.VERFUEGT;
			default: return null;
		}
	}

}
