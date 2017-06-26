package ch.dvbern.ebegu.enums;

/**
 * Enum fuer den Status eines Zahlungsauftrags
 */
public enum ZahlungauftragStatus {

	ENTWURF,
    AUSGELOEST,
	BESTAETIGT;

	public boolean isEntwurf() {
		return ENTWURF.equals(this);
	}

	public boolean isAusgeloest() {
		return AUSGELOEST.equals(this);
	}
}
