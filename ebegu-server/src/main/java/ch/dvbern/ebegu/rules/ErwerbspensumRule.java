package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Berechnet die hoehe des Betreeungspensum einer bestimmten Betreuung
 */
public class ErwerbspensumRule extends AbstractEbeguRule{

	public ErwerbspensumRule(DateRange validityPeriod) {
		super(RuleKey.ERWERBSPENSUM, RuleType.GRUNDREGEL, validityPeriod);
	}

	@Override
	public List<VerfuegungZeitabschnitt> calculate(@Nonnull BetreuungspensumContainer betreuungspensumContainer, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {
		Gesuch gesuch =  betreuungspensumContainer.extractGesuch();
		if(gesuch.getFamiliensituation().hasSecondGesuchsteller()){

		}


		return zeitabschnitte;


	}
}
