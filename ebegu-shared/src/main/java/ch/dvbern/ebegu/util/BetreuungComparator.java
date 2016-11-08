package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungspensum;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Comparator, der die Betreuungen nach folgender Regel sortiert:
 * 1. Die Kita mit dem früherem Startdatum wird zuerst berücksichtigt.
 * 2. Falls beide Angebote dasselbe Startdatum haben, wird die Kita mit dem höheren Pensum berücksichtigt.
 * 3. Falls beide Angebote dasselbe Startdatum und dasselbe Pensum haben, wird die Kita zuerst berücksichtigt, die als erstes erfasst wurde.
 */
public class BetreuungComparator implements Comparator<Betreuung>, Serializable {

	private static final long serialVersionUID = -309383917391346314L;

	@Override
	public int compare(Betreuung betreuung1, Betreuung betreuung2) {

		// Neue Sortierung: Nach Beginn des ersten Betreuungspensums
		List<BetreuungspensumContainer> betreuungenSorted1 = new LinkedList<>(betreuung1.getBetreuungspensumContainers());
		List<BetreuungspensumContainer> betreuungenSorted2 = new LinkedList<>(betreuung2.getBetreuungspensumContainers());

		Collections.sort(betreuungenSorted1, new BetreuungspensumContainerComparator());
		Collections.sort(betreuungenSorted2, new BetreuungspensumContainerComparator());

		if (betreuungenSorted1.isEmpty() || betreuungenSorted2.isEmpty()) {
			return 0;
		}
		//jeweils das erste pensum vergleichen
		Betreuungspensum firstBetreuungspensum1 = betreuungenSorted1.get(0).getBetreuungspensumJA();
		Betreuungspensum firstBetreuungspensum2 = betreuungenSorted2.get(0).getBetreuungspensumJA();

		// Regel 1: Betreuung, die zuerst beginnt
		int result = firstBetreuungspensum1.getGueltigkeit().getGueltigAb().compareTo(firstBetreuungspensum2.getGueltigkeit().getGueltigAb());
		if (result == 0) {
			// Regel 2: Höheres Pensum
			result = firstBetreuungspensum2.getPensum().compareTo(firstBetreuungspensum1.getPensum()); // Absteigend
			if (result == 0) {
				// Regel 3: Reihenfolge der Erfassung
				result = betreuung1.getBetreuungNummer().compareTo(betreuung2.getBetreuungNummer());
			}
		}
		return result;
	}
}
