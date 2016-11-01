package ch.dvbern.ebegu.enums;

/**
 * Enum fuers Feld status in einer Betreuung.
 */
public enum Betreuungsstatus {

	AUSSTEHEND,
	WARTEN,
	SCHULAMT,
	ABGEWIESEN,
	NICHT_EINGETRETEN,
	BESTAETIGT,
	VERFUEGT,
	GESCHLOSSEN_OHNE_VERFUEGUNG;

	public boolean isGeschlossen() {
		return VERFUEGT.equals(this) || GESCHLOSSEN_OHNE_VERFUEGUNG.equals(this);
	}
}
