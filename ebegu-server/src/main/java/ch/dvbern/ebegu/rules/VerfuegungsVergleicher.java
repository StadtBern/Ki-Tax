package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;

import java.util.List;


public class VerfuegungsVergleicher {

	public boolean isSameVerfuegungsdaten(Betreuung betreuung, Gesuch gesuchForMutaion) {

		// Wenn keine Mutation vorhanden ist muss nicht gemerged werden
		if (gesuchForMutaion == null) {
			return false;
		}

		final Betreuung betreuungGSM = VerfuegungUtil.findBetreuungOnGesuchForMuation(betreuung, gesuchForMutaion);
		if (betreuungGSM == null) {
			return false;
		}

		final List<VerfuegungZeitabschnitt> zeitabschnitte = betreuung.getVerfuegung().getZeitabschnitte();
		final List<VerfuegungZeitabschnitt> zeitabschnitteGSM = betreuungGSM.getVerfuegung().getZeitabschnitte();

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
