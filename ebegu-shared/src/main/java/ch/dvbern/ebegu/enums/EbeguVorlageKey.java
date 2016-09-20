package ch.dvbern.ebegu.enums;

/**
 * Keys für die zeitabhängigen E-BEGU-Vorlagen
 */
public enum EbeguVorlageKey {

	// Die erste Mahnung, falls das Gesuch unvollständig eingereicht wurde
	VORLAGE_MAHNUNG_1,

	// Die zweite Mahnung, falls das Gesuch unvollständig eingereicht wurde
	VORLAGE_MAHNUNG_2,

	// Verfügung des Angebots KITA
	VORLAGE_VERFUEGUNG_KITA,

	// Eine Angebotsübergreifende Verfügung Verfügung Kita Verfügung des Angebots Kita
	VORLAGE_NICHT_EINTRETENSVERFUEGUNG,

	// Verfügung des Angebots Tageseltern Kleinkinder
	VORLAGE_VERFUEGUNG_TAGESELTERN_KLEINKINDER,

	// Verfügung (Brief) des Angebots Tagesstaette Schulkinder
	VORLAGE_BRIEF_TAGESSTAETTE_SCHULKINDER,

	// Verfügung (Brief) des Angebots Tageseltern Schulkinder
	VORLAGE_BRIEF_TAGESELTERN_SCHULKINDER,

	// Die Freigabequittung die der Gesuchsteller ausdruckt und ans Jugendamt schickt. Die Freigabequittung geht ans
	// Jugendamt, falls diese NICHT nur das Angebot Tagesschule beinhaltet. Beinhaltet auch das Anlageverzeichnis.
	VORLAGE_FREIGABEQUITTUNG_JUGENDAMT,

	// Die Freigabequittung die der Gesuchsteller ausdruckt und ans Schulamt schickt.
	// Die Freigabequittung geht ans Schulamt, falls dieses NUR das Angebot Tagesschule beinhaltet.
	// Beinhaltet auch das Anlageverzeichnis.
	VORLAGE_FREIGABEQUITTUNG_SCHULAMT,

	// Ein Anhang der mit den Verfügungen mitgeschickt wird und aufzeigt, wie die
	// finanzielle Situation gerechnet wurde
	VORLAGE_FINANZIELLE_SITUATION,

	// Zurückweisung Der Brief, der dem Gesuchsteller bei einer Zurückweisung versendet wird
	VORLAGE_ZURUECKWEISUNG,

	// Ein angebotsübergreifenden neutralen Begleitbrief zu den Verfügungen
	VORLAGE_BEGLEITSCHREIBEN;

	private boolean proGesuchsperiode;

	EbeguVorlageKey() {
		this.proGesuchsperiode = true;
	}

	EbeguVorlageKey(boolean proGesuchsperiode) {
		this.proGesuchsperiode = proGesuchsperiode;
	}

	public boolean isProGesuchsperiode() {
		return proGesuchsperiode;
	}

	public void setProGesuchsperiode(boolean proGesuchsperiode) {
		this.proGesuchsperiode = proGesuchsperiode;
	}
}
