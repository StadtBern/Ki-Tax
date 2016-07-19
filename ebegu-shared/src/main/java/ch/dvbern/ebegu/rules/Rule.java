package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface für alle Berechnungs-Regeln in E-BEGU.
 */
public interface Rule {

	/**
	 * @return Datum von dem an die Regel gilt
	 */
	@Nonnull
	LocalDate validFrom();

	/**
	 * @return Datum bis zu dem die Regel gilt
	 */
	@Nonnull
	LocalDate validTo();

	/**
	 * @param stichtag
	 * @return true wenn die Regel am Strichtag gueltig sit
	 */
	boolean isValid(@Nonnull LocalDate stichtag);

	/**
	 *
	 * @return den {@link RuleType} Enumwert dieser Regel
	 */
	@Nonnull
	RuleType getRuleType();

	/**
	 *
	 * @return einzigartiger Key fuer diese Regel
	 */
	@Nonnull
	RuleKey getRuleKey();

	/**
	 * Diese Methode fuehrt die eigentliche Berechnung durch die von der Regel abgebildet wird
	 * @param betreuung Die Betreuung fuer die Berechnet wird
	 * @param zeitabschnitte Die Zeitabschnitte die bereits ermittelt wurden
	 * @param finSitResultatDTO Die Finanzielle Situation die zu diesem Antrag gehoert
	 * @return gemergete Liste von bestehenden und neu berechneten Zeitabschnitten
	 */
	@Nonnull
	List<VerfuegungZeitabschnitt> calculate(Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte,
											@Nonnull FinanzielleSituationResultateDTO finSitResultatDTO);
}
