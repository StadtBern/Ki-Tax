package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.Verfuegung;

public class VerfuegungUtil {

	public static Verfuegung findVerfuegungOnGesuchForMutation(Betreuung betreuung, Gesuch gesuchForMutation) {

		for (KindContainer kindContainer : gesuchForMutation.getKindContainers()) {
			if (kindContainer.getKindNummer().equals(betreuung.getKind().getKindNummer())) {
				for (Betreuung betreuungGSM : kindContainer.getBetreuungen()) {
					if (betreuungGSM.getBetreuungNummer().equals(betreuung.getBetreuungNummer())) {
						if (betreuungGSM.getVerfuegung() != null) {
							return betreuungGSM.getVerfuegung();
						} else {
							return betreuungGSM.getVorgaengerVerfuegung();
						}
					}
				}
			}
		}
		return null;
	}


}
