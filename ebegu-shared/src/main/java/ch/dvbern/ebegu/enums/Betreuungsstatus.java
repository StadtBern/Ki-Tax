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
	VERFUEGT;

	public boolean isVerfuegt() {
		return VERFUEGT.equals(this);
	}
}
