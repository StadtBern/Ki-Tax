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

	public boolean isAngebotJugendamtKleinkind() {
		return KITA.equals(this) || TAGESELTERN_KLEINKIND.equals(this);
	}

	public boolean isAngebotJugendamtSchulkind() {
		return TAGI.equals(this) || TAGESELTERN_SCHULKIND.equals(this);
	}

	public boolean isJugendamt() {
		return !isSchulamt();
	}
}
