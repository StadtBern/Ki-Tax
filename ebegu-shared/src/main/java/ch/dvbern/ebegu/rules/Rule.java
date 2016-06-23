package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.BGPensumZeitabschnitt;
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


	List<BGPensumZeitabschnitt> calculate(@Nonnull BetreuungspensumContainer betreuungspensumContainer,
										  @Nonnull List<BGPensumZeitabschnitt> zeitabschnitte,
										  @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO);
}
