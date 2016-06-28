package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;

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

	@Nonnull
	RuleType getRuleType();

	@Nonnull
	RuleKey getRuleKey();

	@Nonnull
	List<VerfuegungZeitabschnitt> calculate(@Nonnull BetreuungspensumContainer betreuungspensumContainer,
											@Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte,
											@Nonnull FinanzielleSituationResultateDTO finSitResultatDTO);
}
