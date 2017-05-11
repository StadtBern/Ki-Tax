package ch.dvbern.ebegu.enums;

/**
 * AntragStatus fuer das DTO.
 * Der Status von einem Antrag ist in der DB anders als auf dem Client. Grund dafür ist, dass es bei manchen DB-AntragStatus verschiedene "substatus"
 * gibt. Auf dem Client wird der "substatus" anstatt der status angezeigt.
 * Beispiel:
 * 		Der Status PLATZBESTAETIGUNG_WARTEN wird nur auf dem Client angezeigt, wenn noch mindestens eine Platzanfrage
 * 		ausstehend ist. Auf dem Server bleibt es aber im Status IN_BEARBEITUNG.
 */
public enum AntragStatusDTO {
	IN_BEARBEITUNG_GS,
	FREIGABEQUITTUNG,
	NUR_SCHULAMT,
	NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN,
	FREIGEGEBEN,
	IN_BEARBEITUNG_JA,
	ZURUECKGEWIESEN,
	ERSTE_MAHNUNG,
	ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN,
	ERSTE_MAHNUNG_ABGELAUFEN,
	ZWEITE_MAHNUNG,
	ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN,
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
