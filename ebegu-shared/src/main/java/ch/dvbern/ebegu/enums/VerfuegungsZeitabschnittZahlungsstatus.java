package ch.dvbern.ebegu.enums;

/**
 * Zahlungsstatus fuer VerfuegungsZeitabschnitte
 */
public enum VerfuegungsZeitabschnittZahlungsstatus {

	NEU,
	VERRECHNET,
	IGNORIERT;

	public boolean isNeu() {
		return NEU.equals(this);
	}

	public boolean isVerrechnet() {
		return VERRECHNET.equals(this);
	}

	public boolean isIgnoriert() {
		return IGNORIERT.equals(this);
	}
}
