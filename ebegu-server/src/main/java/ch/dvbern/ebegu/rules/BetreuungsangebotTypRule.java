package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Regel für Betreuungsangebot: Es werden nur die Nicht-Schulamt-Angebote berechnet.
 */
public class BetreuungsangebotTypRule extends AbstractEbeguRule{

	public BetreuungsangebotTypRule(DateRange validityPeriod) {
		super(RuleKey.BETREUUNGSANGEBOT_TYP, RuleType.REDUKTIONSREGEL, validityPeriod);
	}

	@Nonnull
	@Override
	protected Collection<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull BetreuungspensumContainer betreuungspensumContainer, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {
		return new ArrayList<>();
	}

	@Override
	protected void executeRule(@Nonnull BetreuungspensumContainer betreuungspensumContainer, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		if (betreuungspensumContainer.getBetreuung().getInstitutionStammdaten().getBetreuungsangebotTyp().isSchulamt()) {
			// TODO Es braucht noch eine, die für Tagesschule und Tagi immer den gewünschten Anspruch setzt. Evt. im BetreuungspensumRule?
			verfuegungZeitabschnitt.setAnspruchspensumOriginal(0);
			verfuegungZeitabschnitt.addBemerkung(RuleKey.BETREUUNGSANGEBOT_TYP.name() + ": Betreuungsangebot Schulamt");
		}
	}
}
