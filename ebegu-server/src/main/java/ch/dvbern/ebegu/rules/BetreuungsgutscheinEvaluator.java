package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.rechner.AbstractBGRechner;
import ch.dvbern.ebegu.rechner.BGRechnerFactory;
import ch.dvbern.ebegu.rechner.BGRechnerParameterDTO;
import ch.dvbern.ebegu.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the Evaluator that runs all the rules and calculations for a given Antrag to determine the Betreuungsgutschein
 */
public class BetreuungsgutscheinEvaluator {


	private List<Rule> rules = new LinkedList<>();

	private RestanspruchEvaluator restanspruchEvaluator = new RestanspruchEvaluator(Constants.DEFAULT_GUELTIGKEIT);
	private MonatsRule monatsRule = new MonatsRule(Constants.DEFAULT_GUELTIGKEIT);

	public BetreuungsgutscheinEvaluator(List<Rule> rules) {
		this.rules = rules;
	}


	private final Logger LOG = LoggerFactory.getLogger(BetreuungsgutscheinEvaluator.class.getSimpleName());

	public void evaluate(Gesuch testgesuch, BGRechnerParameterDTO bgRechnerParameterDTO) {

		//todo umsetzung berechne FinanzielleSituationResultatDTO, entweder uebergeben oder hier mit service berechnen aus gesuch
		FinanzielleSituationResultateDTO finSitResultatDTO = new FinanzielleSituationResultateDTO(testgesuch, 5, new BigDecimal(1222));

		List<Rule> rulesToRun = findRulesToRunForPeriode(testgesuch.getGesuchsperiode());
		for (KindContainer kindContainer : testgesuch.getKindContainers()) {
			// Pro Kind werden (je nach Angebot) die Anspruchspensen aufsummiert. Wir müssen uns also nach jeder Betreuung
			// den "Restanspruch" merken für die Berechnung der nächsten Betreuung
			List<VerfuegungZeitabschnitt> restanspruchZeitabschnitte = createInitialenRestanspruch(testgesuch.getGesuchsperiode());

			// Betreuungen werden einzeln berechnet
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {

				// Die Initialen Zeitabschnitte sind die "Restansprüche" aus der letzten Betreuung
                List<VerfuegungZeitabschnitt> zeitabschnitte = restanspruchZeitabschnitte;
                for (Rule rule : rulesToRun) {
                    zeitabschnitte = rule.calculate(betreuung, zeitabschnitte, finSitResultatDTO);
                }
                // Nach der Abhandlung dieser Betreuung die Restansprüche für die nächste Betreuung extrahieren
				restanspruchZeitabschnitte = restanspruchEvaluator.createVerfuegungsZeitabschnitte(betreuung, zeitabschnitte, finSitResultatDTO);

				// Nach dem Durchlaufen aller Rules noch die Monatsstückelungen machen
				zeitabschnitte = monatsRule.createVerfuegungsZeitabschnitte(betreuung, zeitabschnitte, finSitResultatDTO);

				// Die Verfügung erstellen
				Verfuegung verfuegung = new Verfuegung();
				verfuegung.setBetreuung(betreuung);
				betreuung.setVerfuegung(verfuegung);

				// Den richtigen Rechner anwerfen
				AbstractBGRechner rechner = BGRechnerFactory.getRechner(betreuung);
				if (rechner != null) {
					for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : zeitabschnitte) {
						rechner.calculate(verfuegungZeitabschnitt, verfuegung, bgRechnerParameterDTO);
					}
				}
				// Und die Resultate in die Verfügung schreiben
                verfuegung.setZeitabschnitte(zeitabschnitte);
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
