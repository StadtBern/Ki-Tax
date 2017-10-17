package ch.dvbern.ebegu.enums;

/**
 * Zahlungsstatus fuer VerfuegungsZeitabschnitte
 */
public enum VerfuegungsZeitabschnittZahlungsstatus {

	NEU,
	VERRECHNET,
	VERRECHNET_KORRIGIERT, // Die Zahlung war schon ausbezahlt, wurde aber in einem späteren Zahlungslauf korrigiert
	IGNORIEREND, // Zahlung ist markiert zum Ignorieren aber es wurde noch nicht "ausbezahlt"
	IGNORIERT, // Zahlung wurde mal als IGNORIEREND markiert und ist auch "ausbezahlt"
	IGNORIERT_KORRIGIERT; // Die Zahlung war schon ignoriert, wurde aber in einem späteren Zahlungslauf korrigiert

	public boolean isNeu() {
		return NEU == this;
	}

	public boolean isVerrechnet() {
		return VERRECHNET == this || VERRECHNET_KORRIGIERT == this;
	}

	public boolean isIgnorierend() {
		return IGNORIEREND == this;
	}

	public boolean isIgnoriert() {
		return IGNORIERT == this || IGNORIERT_KORRIGIERT == this;
	}

	public boolean isIgnoriertIgnorierend() {
		return isIgnorierend() || isIgnoriert();
	}
}
