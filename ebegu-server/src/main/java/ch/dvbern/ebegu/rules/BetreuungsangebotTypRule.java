package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * User: homa
 * Date: 17.06.16
 * comments homa
 */
public class BetreuungsangebotTypRule extends AbstractEbeguRule{

	public BetreuungsangebotTypRule(DateRange validityPeriod) {
		super(RuleKey.BETREUUNGSANGEBOT_TYP, RuleType.REDUKTIONSREGEL, validityPeriod);
	}

	@Override
	public List<VerfuegungZeitabschnitt> calculate(@Nonnull BetreuungspensumContainer betreuungspensumContainer, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {
		BetreuungspensumContainer betreuungspensum = new BetreuungspensumContainer();
		betreuungspensum.getBetreuung().getKind().getGesuch();

		return null;
	}
}
