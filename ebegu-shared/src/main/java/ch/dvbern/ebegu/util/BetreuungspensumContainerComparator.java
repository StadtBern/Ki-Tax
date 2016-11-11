package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.BetreuungspensumContainer;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator, der die Betreuungspensen nach Datum-Von sortiert.
 */
public class BetreuungspensumContainerComparator implements Comparator<BetreuungspensumContainer>, Serializable {

	private static final long serialVersionUID = -309383917391346314L;

	@Override
	public int compare(BetreuungspensumContainer o1, BetreuungspensumContainer o2) {
		return o1.getBetreuungspensumJA().getGueltigkeit().getGueltigAb().compareTo(o2.getBetreuungspensumJA().getGueltigkeit().getGueltigAb());
	}
}
