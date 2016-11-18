package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;

import java.util.List;


public class VerfuegungsVergleicher {

	public boolean isSameVerfuegungsdaten(Betreuung betreuung, Gesuch gesuchForMutation) {

		// Wenn keine Mutation vorhanden ist muss nicht gemerged werden
		if (gesuchForMutation == null) {
			return false;
		}

		final Verfuegung verfuegungOnGesuchForMuation = VerfuegungUtil.findVerfuegungOnGesuchForMutation(betreuung, gesuchForMutation);
		if (verfuegungOnGesuchForMuation == null) {
			return false;
		}

		final List<VerfuegungZeitabschnitt> zeitabschnitte = betreuung.getVerfuegung().getZeitabschnitte();
		final List<VerfuegungZeitabschnitt> zeitabschnitteGSM = verfuegungOnGesuchForMuation.getZeitabschnitte();

		// Wenn unterschiedliche Anzahl Zeitabschnitte hat es Aenderungen
		if (zeitabschnitte.size() != zeitabschnitteGSM.size()) {
			return false;
		}

		for (int i = 0; i < zeitabschnitte.size(); i++) {
			if (!zeitabschnitte.get(i).isSamePersistedValues(zeitabschnitteGSM.get(i))) {
				return false;
			}
		}
		return true;
	}

}
