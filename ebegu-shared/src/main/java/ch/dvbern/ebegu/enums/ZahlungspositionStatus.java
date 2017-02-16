package ch.dvbern.ebegu.enums;

/**
 * Enum fuer den Status eines Zahlungsauftrags.
 */
public enum ZahlungspositionStatus {

	NORMAL,
	KORREKTUR_VOLLKOSTEN,
	KORREKTUR_ELTERNBEITRAG;

	public boolean isAuszuzahlen() {
		return NORMAL.equals(this) || KORREKTUR_VOLLKOSTEN.equals(this);
	}
}
