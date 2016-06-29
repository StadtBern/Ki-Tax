package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.util.Constants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the Evaluator that runs all the rules and calculations for a given Antrag to determine the Berechnungsgutschein
 */
public class BetreuungsgutscheinEvaluator {


	private List<Rule> rules = new LinkedList<>();

	private RestanspruchEvaluator restanspruchEvaluator = new RestanspruchEvaluator(Constants.DEFAULT_GUELTIGKEIT);

	public BetreuungsgutscheinEvaluator(List<Rule> rules) {
		this.rules = rules;
	}

	public void evaluate(Gesuch testgesuch) {

		//todo umsetzung berechne FinanzielleSituationResultatDTO, entweder uebergeben oder hier mit service berechnen aus gesuch
		FinanzielleSituationResultateDTO finSitResultatDTO = new FinanzielleSituationResultateDTO(testgesuch, 5, new BigDecimal(1222));

		List<Rule> rulesToRun = findRulesToRunForPeriode(testgesuch.getGesuchsperiode());
		for (KindContainer kindContainer : testgesuch.getKindContainers()) {
			// Pro Kind werden (je nach Angebot) die Anspruchspensen aufsummiert. Wir müssen uns also nach jeder Betreuung
			// den "Restanspruch" merken für die Berechnung der nächsten Betreuung
			List<VerfuegungZeitabschnitt> restanspruchZeitabschnitte = createInitialenRestanspruch();

			// Betreuungen werden einzeln berechnet
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {

				// Die Initialen Zeitabschnitte sind die "Restansprüche" aus der letzten Betreuung
                List<VerfuegungZeitabschnitt> zeitabschnitte = restanspruchZeitabschnitte;
                for (Rule rule : rulesToRun) {
                    zeitabschnitte = rule.calculate(betreuung, zeitabschnitte, finSitResultatDTO);
                }
                // Nach der Abhandlung dieser Betreuung die Restansprüche für die nächste Betreuung extrahieren
				restanspruchZeitabschnitte = restanspruchEvaluator.createVerfuegungsZeitabschnitte(betreuung, zeitabschnitte, finSitResultatDTO);

				//TODO (hefr) Nach dem Durchlaufen aller Rules noch die Monatsstückelungen machen und die eigentliche Verfügung machen
                Verfuegung verfuegung = new Verfuegung();
                verfuegung.setZeitabschnitte(zeitabschnitte);
                verfuegung.setBetreuung(betreuung);
				betreuung.setVerfuegung(verfuegung);
			}
		}
	}

	private List<Rule> findRulesToRunForPeriode(Gesuchsperiode gesuchsperiode) {
		List<Rule> rulesForGesuchsperiode = new LinkedList<>();
		for (Rule rule : rules) {
			if (rule.isValid(gesuchsperiode.getGueltigkeit().getGueltigAb())) {
				rulesForGesuchsperiode.add(rule);
			}
		}
		return rulesForGesuchsperiode;
	}

	public static List<VerfuegungZeitabschnitt> createInitialenRestanspruch() {
		List<VerfuegungZeitabschnitt> restanspruchZeitabschnitte = new ArrayList<>();
		VerfuegungZeitabschnitt initialerRestanspruch = new VerfuegungZeitabschnitt(Constants.DEFAULT_GUELTIGKEIT);
		initialerRestanspruch.setAnspruchspensumRest(-1); // Damit wir erkennen, ob schon einmal ein "Rest" durch eine Rule gesetzt wurde
		restanspruchZeitabschnitte.add(initialerRestanspruch);
		return restanspruchZeitabschnitte;
	}
}
