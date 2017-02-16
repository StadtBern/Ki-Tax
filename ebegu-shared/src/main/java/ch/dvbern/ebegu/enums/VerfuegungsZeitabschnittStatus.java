package ch.dvbern.ebegu.enums;

/**
 * Zahlungsstatus fuer VerfuegungsZeitabschnitte
 */
public enum VerfuegungsZeitabschnittStatus {

	NEU,
	VERRECHNET,
	IDENTISCH;

	public boolean isNeu() {
		return NEU.equals(this);
	}
}
