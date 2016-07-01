package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;

/**
 * Basisklasse fuer eine Rule
 */
public interface Rule {

	LocalDate validFrom();
	LocalDate validTo();

	RuleType getRuleType();
	RuleKey getRuleKey();


	List<VerfuegungZeitabschnitt> calculate(@Nonnull BetreuungspensumContainer betreuungspensumContainer,
											@Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte,
											@Nonnull FinanzielleSituationResultateDTO finSitResultatDTO);
}
