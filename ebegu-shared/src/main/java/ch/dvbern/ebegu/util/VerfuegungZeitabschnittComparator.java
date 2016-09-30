package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator, der die VerfuegungsZeitabschnitte nach Datum-Von sortiert.
 */
public class VerfuegungZeitabschnittComparator implements Comparator<VerfuegungZeitabschnitt>, Serializable {

	private static final long serialVersionUID = -309383917391346314L;

	@Override
	public int compare(VerfuegungZeitabschnitt o1, VerfuegungZeitabschnitt o2) {
		return o1.getGueltigkeit().compareTo(o2.getGueltigkeit());
	}
}
