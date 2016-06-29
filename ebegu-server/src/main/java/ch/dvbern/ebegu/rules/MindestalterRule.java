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
 * Regel für Mindestalter des Kindes:
 * - Erst ab dem 3. Monat besteht ein Anspruch. Ein Kita-Platz kann aber schon vor dem dritten Monat
 * 		beansprucht werden. In diesem Fall wird die Zeit vor dem 3. Monat zum Privattarif berechnet.
 */
public class MindestalterRule extends AbstractEbeguRule {


	public MindestalterRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.MINDESTALTER, RuleType.REDUKTIONSREGEL, validityPeriod);
	}

	@Nonnull
	@Override
	protected Collection<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull BetreuungspensumContainer betreuungspensumContainer, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {
		return new ArrayList<>();
	}

	@Override
	protected void executeRule(@Nonnull BetreuungspensumContainer betreuungspensumContainer, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {

	}

}
