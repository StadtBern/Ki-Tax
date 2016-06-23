package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.BGPensumZeitabschnitt;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.types.DateRange;

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
	public List<BGPensumZeitabschnitt> calculate(Gesuch antrag) {

		BetreuungspensumContainer betreuungspensum = new BetreuungspensumContainer();
		betreuungspensum.getBetreuung().getKind().getGesuch();

		return null;
	}
}
