package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;

public class VerfuegungUtil {

	public  static Betreuung findBetreuungOnGesuchForMuation(Betreuung betreuung, Gesuch gesuchForMutaion) {

		for (KindContainer kindContainer : gesuchForMutaion.getKindContainers()) {
			if (kindContainer.getKindNummer().equals(betreuung.getKind().getKindNummer())) {
				for (Betreuung betreuungGSM : kindContainer.getBetreuungen()) {
					if (betreuungGSM.getBetreuungNummer().equals(betreuung.getBetreuungNummer())) {
						return betreuungGSM;
					}
				}
			}
		}
		return null;
	}


}
