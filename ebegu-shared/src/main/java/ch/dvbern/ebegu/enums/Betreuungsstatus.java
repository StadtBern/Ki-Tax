package ch.dvbern.ebegu.enums;

/**
 * Enum fuers Feld status in einer Betreuung.
 */
public enum Betreuungsstatus {

	AUSSTEHEND,
	WARTEN,
	SCHULAMT,
	ABGEWIESEN,
	BESTAETIGT,
	VERFUEGT,
	GESCHLOSSEN_OHNE_VERFUEGUNG;

	public boolean isVerfuegt() {
		return VERFUEGT.equals(this);
	}
}
