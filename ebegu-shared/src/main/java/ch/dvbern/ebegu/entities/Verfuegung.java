package ch.dvbern.ebegu.entities;

import java.util.List;

/**
 * Verfuegung pro Betreuung
 */
public class Verfuegung {

	String automatischeInitialisiertteBemerkungen ;
	String manuelleBemerkungen;

	List<VerfuegungZeitabschnitt> zeitabschnitte;
	Betreuung betreuung;


	public String getAutomatischeInitialisiertteBemerkungen() {
		return automatischeInitialisiertteBemerkungen;
	}

	public void setAutomatischeInitialisiertteBemerkungen(String automatischeInitialisiertteBemerkungen) {
		this.automatischeInitialisiertteBemerkungen = automatischeInitialisiertteBemerkungen;
	}

	public String getManuelleBemerkungen() {
		return manuelleBemerkungen;
	}

	public void setManuelleBemerkungen(String manuelleBemerkungen) {
		this.manuelleBemerkungen = manuelleBemerkungen;
	}

	public List<VerfuegungZeitabschnitt> getZeitabschnitte() {
		return zeitabschnitte;
	}

	public void setZeitabschnitte(List<VerfuegungZeitabschnitt> zeitabschnitte) {
		this.zeitabschnitte = zeitabschnitte;
	}

	public Betreuung getBetreuung() {
		return betreuung;
	}

	public void setBetreuung(Betreuung betreuung) {
		this.betreuung = betreuung;
	}
}
