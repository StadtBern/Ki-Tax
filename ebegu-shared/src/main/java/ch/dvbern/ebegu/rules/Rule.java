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

	@Nonnull
	LocalDate validFrom();

	@Nonnull
	LocalDate validTo();

	boolean isValid(@Nonnull LocalDate stichtag);

	@Nonnull
	RuleType getRuleType();

	@Nonnull
	RuleKey getRuleKey();

	@Nonnull
	List<VerfuegungZeitabschnitt> calculate(Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte,
											@Nonnull FinanzielleSituationResultateDTO finSitResultatDTO);
}
