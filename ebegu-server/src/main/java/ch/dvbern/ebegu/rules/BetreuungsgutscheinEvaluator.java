package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.rechner.AbstractBGRechner;
import ch.dvbern.ebegu.rechner.BGRechnerFactory;
import ch.dvbern.ebegu.rechner.BGRechnerParameterDTO;
import ch.dvbern.ebegu.rules.initalizer.RestanspruchInitializer;
import ch.dvbern.ebegu.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is the Evaluator that runs all the rules and calculations for a given Antrag to determine the Betreuungsgutschein
 */
public class BetreuungsgutscheinEvaluator {

	private boolean isDebug = true; // TODO (team) ApplicationProperty machen!

	private List<Rule> rules = new LinkedList<>();

	private RestanspruchInitializer restanspruchInitializer = new RestanspruchInitializer();
	private MonatsRule monatsRule = new MonatsRule(Constants.DEFAULT_GUELTIGKEIT);

	public BetreuungsgutscheinEvaluator(List<Rule> rules) {
		this.rules = rules;
	}

	public BetreuungsgutscheinEvaluator(List<Rule> rules, boolean enableDebugOutput) {
			this.rules = rules;
			this.isDebug = enableDebugOutput;
		}


	private final Logger LOG = LoggerFactory.getLogger(BetreuungsgutscheinEvaluator.class.getSimpleName());

	public void evaluate(Gesuch gesuch, BGRechnerParameterDTO bgRechnerParameterDTO) {

		// Wenn diese Methode aufgerufen wird, muss die Berechnung der Finanzdaten bereits erfolgt sein:
		if (gesuch.getFinanzDatenDTO() == null) {
			throw new IllegalStateException("Bitte zuerst die Finanzberechnung ausführen! -> FinanzielleSituationRechner.calculateFinanzDaten()");
		}

		List<Rule> rulesToRun = findRulesToRunForPeriode(gesuch.getGesuchsperiode());
		for (KindContainer kindContainer : gesuch.getKindContainers()) {
			// Pro Kind werden (je nach Angebot) die Anspruchspensen aufsummiert. Wir müssen uns also nach jeder Betreuung
			// den "Restanspruch" merken für die Berechnung der nächsten Betreuung,
			// am Schluss kommt dann jeweils eine Reduktionsregel die den Anspruch auf den Restanspruch beschraenkt
			List<VerfuegungZeitabschnitt> restanspruchZeitabschnitte = createInitialenRestanspruch(gesuch.getGesuchsperiode());

			// Betreuungen werden einzeln berechnet
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {

				if (Betreuungsstatus.VERFUEGT.equals(betreuung.getBetreuungsstatus())) {
					// Verfuegte Betreuungen duerfen nicht neu berechnet werden
					continue;
				}

				// Die Initialen Zeitabschnitte sind die "Restansprüche" aus der letzten Betreuung
                List<VerfuegungZeitabschnitt> zeitabschnitte = restanspruchZeitabschnitte;
				if (isDebug) {
					LOG.info("BG-Nummer: " + betreuung.getBGNummer());
				}
                for (Rule rule : rulesToRun) {
                    zeitabschnitte = rule.calculate(betreuung, zeitabschnitte);
					if (isDebug) {
						LOG.info(rule.getClass().getSimpleName() + " (" + rule.getRuleKey().name() + ": " + rule.getRuleType().name() + ")");
						for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : zeitabschnitte) {
							LOG.info(verfuegungZeitabschnitt.toString());
						}
					}
				}
                // Nach der Abhandlung dieser Betreuung die Restansprüche für die nächste Betreuung extrahieren
				restanspruchZeitabschnitte = restanspruchInitializer.createVerfuegungsZeitabschnitte(betreuung, zeitabschnitte);

				// Nach dem Durchlaufen aller Rules noch die Monatsstückelungen machen
				zeitabschnitte = monatsRule.createVerfuegungsZeitabschnitte(betreuung, zeitabschnitte);

				// Die Verfügung erstellen
				if (betreuung.getVerfuegung() == null) {
					Verfuegung verfuegung = new Verfuegung();
					betreuung.setVerfuegung(verfuegung);
					verfuegung.setBetreuung(betreuung);
				}

				// Den richtigen Rechner anwerfen
				AbstractBGRechner rechner = BGRechnerFactory.getRechner(betreuung);
				if (rechner != null) {
					for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : zeitabschnitte) {
						rechner.calculate(verfuegungZeitabschnitt, betreuung.getVerfuegung(), bgRechnerParameterDTO);
					}
				}
				// Und die Resultate in die Verfügung schreiben
				betreuung.getVerfuegung().setZeitabschnitte(zeitabschnitte);
				Set<String> bemerkungenOfAbschnitte = zeitabschnitte.stream()
					.map(VerfuegungZeitabschnitt::getBemerkungen)
					.filter(s -> !StringUtils.isEmpty(s)).collect(Collectors.toSet());
				betreuung.getVerfuegung().setGeneratedBemerkungen(String.join(";\n", bemerkungenOfAbschnitte));
			}
		}
	}

	private List<Rule> findRulesToRunForPeriode(Gesuchsperiode gesuchsperiode) {
		List<Rule> rulesForGesuchsperiode = new LinkedList<>();
		for (Rule rule : rules) {
			if (rule.isValid(gesuchsperiode.getGueltigkeit().getGueltigAb())) {
				rulesForGesuchsperiode.add(rule);
			} else{
				LOG.debug("Rule did not aply to Gesuchsperiode " + rule);

			}
		}
		return rulesForGesuchsperiode;
	}

	public static List<VerfuegungZeitabschnitt> createInitialenRestanspruch(Gesuchsperiode gesuchsperiode) {
		List<VerfuegungZeitabschnitt> restanspruchZeitabschnitte = new ArrayList<>();
		VerfuegungZeitabschnitt initialerRestanspruch = new VerfuegungZeitabschnitt(gesuchsperiode.getGueltigkeit());
		initialerRestanspruch.setAnspruchspensumRest(-1); // Damit wir erkennen, ob schon einmal ein "Rest" durch eine Rule gesetzt wurde
		restanspruchZeitabschnitte.add(initialerRestanspruch);
		return restanspruchZeitabschnitte;
	}
}
