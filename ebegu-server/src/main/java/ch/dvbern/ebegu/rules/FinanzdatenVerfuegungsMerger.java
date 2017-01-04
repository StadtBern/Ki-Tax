package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Bei Wechsel von 1 auf 2, sowie von 2 auf 1 Gesuchsteller muss die Finanzielle Situation ab Folgemonat des Ereignisses
 * angepasst werden
 */
public class FinanzdatenVerfuegungsMerger {

	private static final Logger LOG = LoggerFactory.getLogger(FinanzdatenVerfuegungsMerger.class.getSimpleName());

	/**
	 * Um code lesbar zu halten wird die Regel PMD.CollapsibleIfStatements ausgeschaltet
	 */
	@Nonnull
	@SuppressWarnings("PMD.CollapsibleIfStatements")
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, Gesuch gesuchForMutaion) {

		// Wenn keine Mutation vorhanden ist muss nicht gemerged werden
		if (gesuchForMutaion == null) {
			return zeitabschnitte;
		}

		final Verfuegung verfuegungOnGesuchForMuation = VerfuegungUtil.findVerfuegungOnGesuchForMutation(betreuung, gesuchForMutaion);
		if (verfuegungOnGesuchForMuation == null) {
			return zeitabschnitte;
		}

		final LocalDate ereignisDatum = betreuung.extractGesuch().getFamiliensituationContainer().getFamiliensituationJA().getAenderungPer();
		if (ereignisDatum == null) {
			return zeitabschnitte;
		}
		final LocalDate ereignisStichtag = ereignisDatum.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());

		List<VerfuegungZeitabschnitt> monatsSchritte = new ArrayList<>();

		for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : zeitabschnitte) {
			final LocalDate zeitabschnittStart = verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb();
			VerfuegungZeitabschnitt zeitabschnitt = copy(verfuegungZeitabschnitt);

			if (!betrifftNeueDaten(verfuegungZeitabschnitt, ereignisStichtag)) {
				// Der Zeitabschnitt ist noch vor der Mutation der Familiensituation. Es zaehlen die alten Daten
				VerfuegungZeitabschnitt zeitabschnittAusErstgesuch = findRelevantenZeitabschnitt(zeitabschnittStart, verfuegungOnGesuchForMuation);
				zeitabschnitt.setEinkommensjahr(zeitabschnittAusErstgesuch.getEinkommensjahr());
				zeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(zeitabschnittAusErstgesuch.getMassgebendesEinkommenVorAbzFamgr());
				//TODO (hefr) Gibts hier eine Bemerkung?
				zeitabschnitt.setBemerkungen(zeitabschnittAusErstgesuch.getBemerkungen());
				zeitabschnitt.addBemerkung(RuleKey.FINANZDATEN_VOR_MUATION_FAMILIENSITUATION, MsgKey.FINANZDATEN_VOR_MUATION_FAMILIENSITUATION);

			} else {
				// Keine Anpassung notwendig. Wir rechnen bereits mit den richtigen Daten
			}

			monatsSchritte.add(zeitabschnitt);
		}

		return monatsSchritte;
	}

	private boolean betrifftNeueDaten(VerfuegungZeitabschnitt verfuegungZeitabschnitt, LocalDate mutationsEingansdatum) {
		return !verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb().withDayOfMonth(1).isBefore((mutationsEingansdatum));
	}

	private VerfuegungZeitabschnitt findRelevantenZeitabschnitt(LocalDate zeitabschnittStart, Verfuegung verfuegungGSM) {
		for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : verfuegungGSM.getZeitabschnitte()) {
			final DateRange gueltigkeit = verfuegungZeitabschnitt.getGueltigkeit();
			if (gueltigkeit.contains(zeitabschnittStart) || gueltigkeit.startsSameDay(zeitabschnittStart)) {
				return verfuegungZeitabschnitt;
			}
		}
		LOG.error("Zeitabschnitt beim Gesuch für Mutation konnte nicht gefunden werden");
		return null;
	}

	private VerfuegungZeitabschnitt copy(VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(verfuegungZeitabschnitt);
		return zeitabschnitt;
	}
}
