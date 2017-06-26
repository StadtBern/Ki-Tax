package ch.dvbern.ebegu.enums;

/**
 * Zahlungsstatus fuer VerfuegungsZeitabschnitte
 */
public enum VerfuegungsZeitabschnittZahlungsstatus {

	NEU,
	VERRECHNET,
	IGNORIEREND, // Zahlung ist markiert zum Ignorieren aber es wurde noch nicht "ausbezahlt"
	IGNORIERT; // Zahlung wurde mal als IGNORIEREND markiert und ist auch "ausbezahlt"

	public boolean isNeu() {
		return NEU.equals(this);
	}

	public boolean isVerrechnet() {
		return VERRECHNET.equals(this);
	}

	public boolean isIgnorierend() {
		return IGNORIEREND.equals(this);
	}

	public boolean isIgnoriert() {
		return IGNORIERT.equals(this);
	}

	public boolean isIgnoriertIgnorierend() {
		return isIgnorierend() || isIgnoriert();
	}
}
