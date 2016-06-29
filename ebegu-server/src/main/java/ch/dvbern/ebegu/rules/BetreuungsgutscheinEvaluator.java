package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the Evaluator that runs all the rules and calculations for a given Antrag to determine the Berechnungsgutschein
 */
public class BetreuungsgutscheinEvaluator {


	private List<Rule> rules = new LinkedList<>();

	public BetreuungsgutscheinEvaluator(List<Rule> rules) {
		this.rules = rules;
	}

	public void evaluate(Gesuch testgesuch) {

		//todo umsetzung berechne FinanzielleSituationResultatDTO, entweder uebergeben oder hier mit service berechnen aus gesuch
		FinanzielleSituationResultateDTO finSitResultatDTO = new FinanzielleSituationResultateDTO(testgesuch, 5, new BigDecimal(1222));

		List<Rule> rulesToRun = findRulesToRunForPeriode(testgesuch.getGesuchsperiode());
		for (KindContainer kindContainer : testgesuch.getKindContainers()) {
			// Betreuungen werden einzeln berechnet
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				//TODO (hefr) Müsste die Rule nicht für alle Betreuungspensen einer Betreuung sein? Sonst gibt es ja pro Rule nur einen Abschnitt. Und wir können nicht aufgrund Eingangsdatum verschieben!


//				for (BetreuungspensumContainer betreuungspensumContainer : betreuung.getBetreuungspensumContainers()) {
					List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();
					for (Rule rule : rulesToRun) {
						zeitabschnitte = rule.calculate(betreuung, zeitabschnitte, finSitResultatDTO);
					}
					//TODO (hefr) Nach dem Durchlaufen aller Rules noch die Monatsstückelungen machen und die eigentliche Verfügung machen
					Verfuegung verfuegung = new Verfuegung();
					verfuegung.setZeitabschnitte(zeitabschnitte);
					verfuegung.setBetreuung(betreuung);
//				}
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
}
