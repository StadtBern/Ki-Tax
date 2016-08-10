package ch.dvbern.ebegu.enums;

/**
 * Enum fuers Feld betreuungsangebotTyp in Institution.
 */
public enum BetreuungsangebotTyp {
	KITA,
	TAGESSCHULE,
	TAGESELTERN_KLEINKIND,
	TAGESELTERN_SCHULKIND,
	TAGI;

	public boolean isSchulamt() {
		return TAGESSCHULE.equals(this);
	}

	public boolean isTageseltern() {
		return TAGESELTERN_KLEINKIND.equals(this) || TAGESELTERN_SCHULKIND.equals(this);
	}
}
