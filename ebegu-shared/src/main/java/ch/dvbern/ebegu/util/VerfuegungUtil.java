package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Allgemeine Utils fuer Verfuegung
 */
public class VerfuegungUtil {


	/**
	 * Fuer die gegebene DateRange wird berechnet, wie viel Verguenstigung es insgesamt berechnet wurde.
	 * Diese wird dann als BigDecimal zurueckgegeben
	 * Formel: Verguenstigung * (overlappedDays / totalDays)
	 */
	public static BigDecimal getVerguenstigungZeitInterval(@Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull DateRange interval) {
		BigDecimal totalVerguenstigung = BigDecimal.ZERO;
		for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnitte) {
			final DateRange abschnittGueltigkeit = zeitabschnitt.getGueltigkeit();

			final Optional<DateRange> overlap = interval.getOverlap(abschnittGueltigkeit);
			if (overlap.isPresent()) { // only if there is overlap is it needed to calculate
				final BigDecimal overlappedDays = new BigDecimal(overlap.get().getDays());
				final BigDecimal totalAbschnittDays = new BigDecimal(abschnittGueltigkeit.getDays());
				final BigDecimal rate = overlappedDays.divide(totalAbschnittDays, 5, RoundingMode.HALF_UP);
				final BigDecimal overlappedElternbeitrag = zeitabschnitt.getElternbeitrag().multiply(rate);
				final BigDecimal overlappedVollkosten = zeitabschnitt.getVollkosten().multiply(rate);
				final BigDecimal verguenstigung = overlappedVollkosten.subtract(overlappedElternbeitrag);
				totalVerguenstigung = totalVerguenstigung.add(verguenstigung);
			}
		}
		return totalVerguenstigung.setScale(2, RoundingMode.HALF_UP);
	}
}
