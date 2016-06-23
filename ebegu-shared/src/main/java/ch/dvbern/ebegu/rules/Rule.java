package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;

/**
 * User: homa
 * Date: 17.06.16
 * comments homa
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
