package ch.dvbern.ebegu.tests.rules;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinConfigurator;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.rules.Rule;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test der als Proof of Concept dienen soll fuer das Regelsystem
 */
public class BetreuungsgutscheinEvaluatorTest {


	private BetreuungsgutscheinEvaluator evaluator;

	@Before
	public void setUpCalcuator() {
		Map<EbeguParameterKey, EbeguParameter> ebeguParameter = new HashMap<>();
		BetreuungsgutscheinConfigurator configurator = new BetreuungsgutscheinConfigurator();
		List<Rule> rules = configurator.configureRulesForMandant(null, ebeguParameter);
		evaluator = new BetreuungsgutscheinEvaluator(rules);


/**    Idee. Wir iterieren der Reihe nach zuerst ueber alle Kinder dann ueber deren Betreeungspensen. Fuer jedes
 * Betreuungspensum stossen wir die Berechnung an. Der output einer solchen Berechnung ist eine Liste von
 * Zeitabschnitten mit dem
 *
 */


	}

//	//todo das hier in einem service zusammenstellen
//	public void useBernerRules() {
//
//		ErwerbspensumRule betreuungsangebotTypRule = new ErwerbspensumRule(defaultGueltigkeit);
//		rules.put(betreuungsangebotTypRule.getRuleKey(), betreuungsangebotTypRule);
//
//		//todo default gueltikeit mit BetPeriode ersetzten
//		MaximalesEinkommenRule maxEinkommenRule = new MaximalesEinkommenRule(defaultGueltigkeit, new BigDecimal(158000));
//		rules.put(maxEinkommenRule.getRuleKey(), maxEinkommenRule);
//
//		//todo alle weiteren rules hinzufuegen
//
//	}

	@Test
	public void doTestEvaluation(){
		Gesuch testgesuch = new Gesuch();
		evaluator.evaluate(testgesuch);
	}


}
