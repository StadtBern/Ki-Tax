package ch.dvbern.ebegu.enums;

/**
 * Keys für die zeitabhängigen E-BEGU-Vorlagen
 */
public enum EbeguVorlageKey {

	// Die erste Mahnung, falls das Gesuch unvollständig eingereicht wurde
	VORLAGE_MAHNUNG_1("/vorlagen/1_Mahnung.docx"),

	// Die zweite Mahnung, falls das Gesuch unvollständig eingereicht wurde
	VORLAGE_MAHNUNG_2("/vorlagen/2_Mahnung.docx"),

	// Verfügung des Angebots KITA
	VORLAGE_VERFUEGUNG_KITA("/vorlagen/Verfuegungsmuster_kita.docx"),

	// Eine Angebotsübergreifende Verfügung Verfügung Kita Verfügung des Angebots Kita
	VORLAGE_NICHT_EINTRETENSVERFUEGUNG("/vorlagen/Nichteintretensverfuegung.docx"),

	// Vorlage fuer Nicht-Eintreten bei Schulkindern
	VORLAGE_INFOSCHREIBEN_MAXIMALTARIF("/vorlagen/Infoschreiben_Maxtarif.docx"),

	// Verfügung des Angebots Tageseltern Kleinkinder
	VORLAGE_VERFUEGUNG_TAGESELTERN_KLEINKINDER("/vorlagen/Verfuegungsmuster_tageseltern_kleinkinder.docx"),

	// Verfügung (Brief) des Angebots Tagesstaette Schulkinder
	VORLAGE_BRIEF_TAGESSTAETTE_SCHULKINDER("/vorlagen/Verfuegungsmuster_tagesstaette_schulkinder.docx"),

	// Verfügung (Brief) des Angebots Tageseltern Schulkinder
	VORLAGE_BRIEF_TAGESELTERN_SCHULKINDER("/vorlagen/Verfuegungsmuster_tageseltern_schulkinder.docx"),

	// Die Freigabequittung die der Gesuchsteller ausdruckt
	VORLAGE_FREIGABEQUITTUNG("/vorlagen/Freigabequittung.docx"),

	// Ein Anhang der mit den Verfügungen mitgeschickt wird und aufzeigt, wie die
	// finanzielle Situation gerechnet wurde
	VORLAGE_FINANZIELLE_SITUATION("/vorlagen/Berechnungsgrundlagen.docx"),

	// Ein angebotsübergreifenden neutralen Begleitbrief zu den Verfügungen
	VORLAGE_BEGLEITSCHREIBEN("/vorlagen/Begleitschreiben.docx"),

	// Benutzerhandbuecher
	VORLAGE_BENUTZERHANDBUCH_ADMIN("/benutzerhandbuch/Ki-Tax Benutzerhandbuch - Rolle Administrator.pdf");


	private boolean proGesuchsperiode;

	private String defaultVorlagePath;


	EbeguVorlageKey() {
		this.proGesuchsperiode = true;
	}

	EbeguVorlageKey(boolean proGesuchsperiode) {
		this.proGesuchsperiode = proGesuchsperiode;
	}

	EbeguVorlageKey(String defaultVorlagePath) {
		this.proGesuchsperiode = true;
		this.defaultVorlagePath = defaultVorlagePath;
	}

	EbeguVorlageKey(boolean proGesuchsperiode, String defaultVorlagePath) {
		this.proGesuchsperiode = proGesuchsperiode;
		this.defaultVorlagePath = defaultVorlagePath;
	}

	public boolean isProGesuchsperiode() {
		return proGesuchsperiode;
	}

	public void setProGesuchsperiode(boolean proGesuchsperiode) {
		this.proGesuchsperiode = proGesuchsperiode;
	}

	public String getDefaultVorlagePath() {
		return defaultVorlagePath;
	}

	public void setDefaultVorlagePath(String defaultVorlagePath) {
		this.defaultVorlagePath = defaultVorlagePath;
	}
}
