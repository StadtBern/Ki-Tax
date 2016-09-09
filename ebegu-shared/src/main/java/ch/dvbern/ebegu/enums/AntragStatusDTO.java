package ch.dvbern.ebegu.enums;

/**
 * AntragStatus fuer das DTO.
 * Der Status von einem Antrag ist in der DB anders als auf dem Client. Grund dafür ist, dass es bei manchen DB-AntragStatus verschiedene "substatus"
 * gibt. Auf dem Client wird der "substatus" anstatt der status angezeigt.
 * Beispiel:
 * 		Wenn der ERSTE_MAHNUNG geschickt wird, ist der Status ERSTE_MAHNUNG. Wenn der GS dann ein Dokument Hochlaedt, ist der Status immernoch ERSTE_MAHNUNG
 * 		aber auf dem Client  zeigen wir DOKUMENTE_HOCHGELADEN, damit das JA weiss, dass sich etwas geaendert hat und sie etwas machen muessen
 */
public enum AntragStatusDTO {
	IN_BEARBEITUNG_GS,
	FREIGABEQUITTUNG,
	NUR_SCHULAMT,
	FREIGEGEBEN,
	IN_BEARBEITUNG_JA,
	ZURUECKGEWIESEN,
	ERSTE_MAHNUNG,
	ERSTE_MAHNUNG_ABGELAUFEN,
	ZWEITE_MAHNUNG,
	ZWEITE_MAHNUNG_ABGELAUFEN,
	GEPRUEFT,
	VERFUEGEN,
	VERFUEGT
}
