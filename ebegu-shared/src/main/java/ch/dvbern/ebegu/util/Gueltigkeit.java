package ch.dvbern.ebegu.util;


import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.Comparator;

public interface Gueltigkeit {

	/**
	 * Compare entities by their gueltigAb property (standard LocalDate compareTo)
	 */
	@Nonnull
	Comparator<Gueltigkeit> GUELTIG_AB_COMPARATOR = (e1, e2) -> e1.getGueltigkeit().getGueltigAb().compareTo(e2.getGueltigkeit().getGueltigAb());

	@Nonnull
	DateRange getGueltigkeit();
}
