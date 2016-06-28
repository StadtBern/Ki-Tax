package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the Evaluator that runs all the rules and calculations for a given Antrag to determine the Berechnungsgutschein
 */
public class BerechnungsgutscheinEvaluator {


	private final DateRange defaultGueltigkeit = new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME);

	//todo homa hier evtl besser eine Liste
	private Map<RuleKey, Rule> rules = new LinkedHashMap<>();


	public BerechnungsgutscheinEvaluator() {
	}


	//todo das hier in einem service zusammenstellen
	public void useBernerRules() {

		ErwerbspensumRule betreuungsangebotTypRule = new ErwerbspensumRule(defaultGueltigkeit);
		rules.put(betreuungsangebotTypRule.getRuleKey(), betreuungsangebotTypRule);

		//todo default gueltikeit mit BetPeriode ersetzten
		MaximalesEinkommenRule maxEinkommenRule = new MaximalesEinkommenRule(defaultGueltigkeit, new BigDecimal(158000));
		rules.put(maxEinkommenRule.getRuleKey(), maxEinkommenRule);

		//todo alle weiteren rules hinzufuegen

	}

	public void evaluate(Gesuch testgesuch) {

		//todo umsetzung berechne FinanzielleSituationResultatDTO, entweder uebergeben oder hier mit service berechnen aus gesuch
		FinanzielleSituationResultateDTO finSitResultatDTO = new FinanzielleSituationResultateDTO(testgesuch, 5, new BigDecimal(1222));
		Map<RuleKey, Rule> rulesToRun = findRulesToRunForPeriode(testgesuch.getGesuchsperiode());


		for (KindContainer kindContainer : testgesuch.getKindContainers()) {
			List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();


			//Betreuungen werden einzeln berechnet
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				for (BetreuungspensumContainer betreuungspensumContainer : betreuung.getBetreuungspensumContainers()) {

					for (Rule rule : rulesToRun.values()) {
						zeitabschnitte = rule.calculate(betreuungspensumContainer, zeitabschnitte, finSitResultatDTO);
					}

				}

			}
		}


	}

	private Map<RuleKey, Rule> findRulesToRunForPeriode(Gesuchsperiode gesuchsperiode) {


		//bes
		return rules;
	}
}
