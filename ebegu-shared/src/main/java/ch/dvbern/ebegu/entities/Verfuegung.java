package ch.dvbern.ebegu.entities;

import java.util.List;

/**
 * Verfuegung pro Betreuung
 */
public class Verfuegung {

	String automatischeInitialisiertteBemerkungen ;
	String manuelleBemerkungen;

	List<BGPensumZeitabschnitt> zeitabschnitte;
	Betreuung betreuung;

}
