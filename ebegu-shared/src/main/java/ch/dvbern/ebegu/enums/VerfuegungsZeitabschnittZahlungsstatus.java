package ch.dvbern.ebegu.enums;

/**
 * Zahlungsstatus fuer VerfuegungsZeitabschnitte
 */
public enum VerfuegungsZeitabschnittZahlungsstatus {

	NEU,
	VERRECHNET,
	IDENTISCH;

	public boolean isNeu() {
		return NEU.equals(this);
	}

	public boolean isVerrechnet() {
		return VERRECHNET.equals(this);
	}
}
