package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Regel für Mindestalter des Kindes:
 * - Erst ab dem 3. Monat besteht ein Anspruch. Ein Kita-Platz kann aber schon vor dem dritten Monat
 * 		beansprucht werden. In diesem Fall wird die Zeit vor dem 3. Monat zum Privattarif berechnet.
 *
 * 	Verweis 16.12.1 Mindestalter
 */
public class MindestalterRule extends AbstractEbeguRule {


	public MindestalterRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.MINDESTALTER, RuleType.REDUKTIONSREGEL, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {
		return new ArrayList<>();
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {

	}

}
