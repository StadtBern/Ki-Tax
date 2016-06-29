package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Regel für Betreuungsangebot: Es werden nur die Nicht-Schulamt-Angebote berechnet.
 */
public class BetreuungsangebotTypRule extends AbstractEbeguRule {

	public BetreuungsangebotTypRule(DateRange validityPeriod) {
		super(RuleKey.BETREUUNGSANGEBOT_TYP, RuleType.REDUKTIONSREGEL, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {
		return new ArrayList<>();
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		if (betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp().isSchulamt()) {
			verfuegungZeitabschnitt.setAnspruchspensumOriginal(0);
			verfuegungZeitabschnitt.addBemerkung(RuleKey.BETREUUNGSANGEBOT_TYP.name() + ": Betreuungsangebot Schulamt");
		}
	}
}
