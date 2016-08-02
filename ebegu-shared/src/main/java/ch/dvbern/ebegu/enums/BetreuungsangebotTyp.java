package ch.dvbern.ebegu.enums;

/**
 * Enum fuers Feld betreuungsangebotTyp in Institution.
 */
public enum BetreuungsangebotTyp {
	KITA,
	TAGESSCHULE,
	TAGESELTERN,
	TAGI;

	public boolean isSchulamt() {
		return TAGESSCHULE.equals(this);
	}
}
