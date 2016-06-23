package ch.dvbern.ebegu.tests.rules;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.rules.BerechnungsgutscheinEvaluator;
import org.junit.Before;
import org.junit.Test;

/**
 * Test der als Proof of Concept dienen soll fuer das Regelsystem
 */
public class BerechnungsgutscheinEvaluatorTest {


	private BerechnungsgutscheinEvaluator evaluator;

	@Before
	public void setUpCalcuator() {

		evaluator = new BerechnungsgutscheinEvaluator();
		evaluator.useBernerRules();




/**    Idee. Wir iterieren der Reihe nach zuerst ueber alle Kinder dann ueber deren Betreeungspensen. Fuer jedes
 * Betreuungspensum stossen wir die Berechnung an. Der output einer solchen Berechnung ist eine Liste von
 * Zeitabschnitten mit dem
 *
 */


	}

	@Test
	public void doTestEvaluation(){
		Gesuch testgesuch = new Gesuch();
		evaluator.evaluate(testgesuch);
	}


}
