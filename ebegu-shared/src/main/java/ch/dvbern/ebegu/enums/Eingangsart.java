package ch.dvbern.ebegu.enums;

/**
 * Enum fuer die Eingangsart (Papiergesuch vs. Onlinegesuch)
 */
public enum Eingangsart {

	ONLINE, // Von GS erfasst
	PAPIER; // Von JA erfasst

	public boolean isOnlineGesuch() {
		return ONLINE.equals(this);
	}

	public boolean isPapierGesuch() {
		return PAPIER.equals(this);
	}
}
