package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.types.DateRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Regel für Wohnsitz in Bern (Zuzug und Wegzug):
 * - Durch Adresse definiert
 * - Anspruch vom ersten Tag des Zuzugs
 * - Anspruch bis 2 Monate nach Wegzug, auf Ende Monat
 * Verweis 16.8 Der zivilrechtliche Wohnsitz
 */
public class WohnsitzAbschnittRule extends AbstractAbschnittRule {

	private final Logger LOG = LoggerFactory.getLogger(WohnsitzAbschnittRule.class.getSimpleName());


	public WohnsitzAbschnittRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.WOHNSITZ, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> analysedAbschnitte = new ArrayList<>();
		Gesuch gesuch =  betreuung.extractGesuch();
		if (gesuch.getGesuchsteller1() != null) {
			List<VerfuegungZeitabschnitt> adressenAbschnitte = new ArrayList<>();
			adressenAbschnitte.addAll(getAdresseAbschnittForGesuchsteller(gesuch.getGesuchsteller1(), true));
			analysedAbschnitte.addAll(analyseAdressAbschnitte(betreuung, adressenAbschnitte, true));
		}
		if (gesuch.getGesuchsteller2() != null) {
			List<VerfuegungZeitabschnitt> adressenAbschnitte = new ArrayList<>();
			adressenAbschnitte.addAll(getAdresseAbschnittForGesuchsteller(gesuch.getGesuchsteller2(), false));
			analysedAbschnitte.addAll(analyseAdressAbschnitte(betreuung, adressenAbschnitte, false));
		}
		return analysedAbschnitte;
	}

	private List<VerfuegungZeitabschnitt> analyseAdressAbschnitte(Betreuung betreuung, List<VerfuegungZeitabschnitt> adressenAbschnitte, boolean gs1) {
		List<VerfuegungZeitabschnitt> result = new ArrayList<>();
		List<VerfuegungZeitabschnitt> zeitabschnittList = super.mergeZeitabschnitte(adressenAbschnitte);
		zeitabschnittList = super.normalizeZeitabschnitte(zeitabschnittList, betreuung.extractGesuchsperiode());
		VerfuegungZeitabschnitt lastAdresse = null;
		boolean isFirstAbschnitt = true;
		for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnittList) {
			if (isFirstAbschnitt) {
				// Der erste Abschnitt. Wir wissen noch nicht, ob Zuzug oder Wegzug
				isFirstAbschnitt = false;
				lastAdresse = zeitabschnitt;
				result.add(zeitabschnitt);
			} else {
				// Dies ist mindestens die zweite Adresse -> pruefen, ob sich an der Wohnsitz-Situation etwas geaendert hat.
				if (isWohnsitzNichtInGemeinde(lastAdresse, gs1) != isWohnsitzNichtInGemeinde(zeitabschnitt, gs1)) {
					// Es hat geaendert. Was war es fuer eine Anpassung?
					if (isWohnsitzNichtInGemeinde(zeitabschnitt, gs1)) {
						// Es ist ein Wegzug
						LOG.info("Wegzug");
						LocalDate stichtagEndeAnspruch = zeitabschnitt.getGueltigkeit().getGueltigAb().minusDays(1).plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
						lastAdresse.getGueltigkeit().setGueltigBis(stichtagEndeAnspruch);
						if (zeitabschnitt.getGueltigkeit().getGueltigBis().isAfter(stichtagEndeAnspruch.plusDays(1))) {
							zeitabschnitt.getGueltigkeit().setGueltigAb(stichtagEndeAnspruch.plusDays(1));
							result.add(zeitabschnitt);
						}
					} else {
						// Es ist ein Zuzug
						LOG.info("Zuzug");
						result.add(zeitabschnitt);
					}
				} else {
					// Dieser Fall sollte gar nicht eintreten, da die Zeitabschnitte vorher gemergt wurden!
					LOG.info("Zweiter Adressen-Abschnitt mit gleichen Daten: Dieser Fall sollte gar nicht eintreten, da die Zeitabschnitte vorher gemergt wurden!");
					result.add(zeitabschnitt);
				}
			}
		}
		return result;
	}

	boolean isWohnsitzNichtInGemeinde(VerfuegungZeitabschnitt zeitabschnitt, boolean gs1) {
		if (gs1) {
			return zeitabschnitt.isWohnsitzNichtInGemeindeGS1();
		}
		return zeitabschnitt.isWohnsitzNichtInGemeindeGS2();
	}


	/**
	 * geht durch die Erwerpspensen des Gesuchstellers und gibt Abschnitte zurueck
	 * @param gesuchsteller Der Gesuchsteller dessen Erwerbspensumcontainers zu Abschnitte konvertiert werden
	 */
	@Nonnull
	private List<VerfuegungZeitabschnitt> getAdresseAbschnittForGesuchsteller(@Nonnull Gesuchsteller gesuchsteller, boolean gs1) {
		List<VerfuegungZeitabschnitt> adressenZeitabschnitte = new ArrayList<>();
		List<GesuchstellerAdresse> gesuchstellerAdressen = gesuchsteller.getAdressen();
		for (GesuchstellerAdresse gesuchstellerAdresse : gesuchstellerAdressen) {
			VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(gesuchstellerAdresse.getGueltigkeit());
			if (gs1) {
				zeitabschnitt.setWohnsitzNichtInGemeindeGS1(gesuchstellerAdresse.isNichtInGemeinde());
			} else {
				zeitabschnitt.setWohnsitzNichtInGemeindeGS2(gesuchstellerAdresse.isNichtInGemeinde());
			}
			adressenZeitabschnitte.add(zeitabschnitt);
		}
		return adressenZeitabschnitte;
	}
}
