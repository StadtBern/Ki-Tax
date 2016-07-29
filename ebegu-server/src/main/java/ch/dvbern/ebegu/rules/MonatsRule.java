package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Sonderregel die nach der eigentlich Berechnung angewendet wird und welche die Zeitabschnitte auf Monate begrenzt
 */
public class MonatsRule extends AbstractEbeguRule {


	public MonatsRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.NO_RULE, RuleType.NO_RULE, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> monatsSchritte = new ArrayList<>();
		for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnitte) {
			LocalDate gueltigAb = zeitabschnitt.getGueltigkeit().getGueltigAb();
			LocalDate gueltigBis = zeitabschnitt.getGueltigkeit().getGueltigBis();
			while (!gueltigAb.isAfter(gueltigBis)) {
				LocalDate endOfMoth = gueltigAb.with(TemporalAdjusters.lastDayOfMonth());
				LocalDate enddate = endOfMoth.isAfter(gueltigBis) ? gueltigBis : endOfMoth;
				VerfuegungZeitabschnitt monatsSchritt = new VerfuegungZeitabschnitt(new DateRange(gueltigAb, enddate));
				monatsSchritt.add(zeitabschnitt);
				monatsSchritte.add(monatsSchritt);
				gueltigAb = monatsSchritt.getGueltigkeit().getGueltigBis().plusDays(1);
			}
		}
		return monatsSchritte;
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		// Keine Regel
	}
}
