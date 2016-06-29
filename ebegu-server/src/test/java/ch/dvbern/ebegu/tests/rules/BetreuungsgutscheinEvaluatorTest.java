package ch.dvbern.ebegu.tests.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinConfigurator;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.rules.Rule;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Test der als Proof of Concept dienen soll fuer das Regelsystem
 * Testfall, welcher möglichst viele Regeln abhandeln soll:
 *
 * Gesuchsperiode            |----------------------------------------------------------|
 *
 * Erwerbspensum GS1  |------------------ 50 -----------------|
 *                                               |---------------------- 30 ------------|
 * Erwerbspensum GS2         |-------------------- 90 + 10 ----------------------------|
 *
 * Wegzug                                                                       |---------------- - -
 *
 * Kind1                     |--------------- Kita 60, ---------------------------------|
 *                           |------------ Fachstelle 80 --------------|
 *
 * Kind2                     |-------------- Kita1 20 ----------------------------------|
 *                           |-------------- Kita2 40 ----------------------------------|
 *
 * Kind3                     |-------------- Schulamt 100 ------------------------------|
 *
 * Kind4                     |---------------- Kita 30 ---------------------------------|
 *                                 |---- - - Kind 3 Monate alt - - -
 */
public class BetreuungsgutscheinEvaluatorTest {


	private BetreuungsgutscheinEvaluator evaluator;

	private DateRange erwerbspensumGS1_1 = new DateRange(LocalDate.of(2010, Month.FEBRUARY, 2), LocalDate.of(2017, Month.MARCH, 20));
	private DateRange erwerbspensumGS1_2 = new DateRange(LocalDate.of(2017, Month.JANUARY, 1), LocalDate.of(2017, Month.JULY, 31));

	private DateRange gesuchsperiode = new DateRange(LocalDate.of(2016, Month.AUGUST, 1), LocalDate.of(2017, Month.JULY, 31));


	@Before
	public void setUpCalcuator() {
		Map<EbeguParameterKey, EbeguParameter> ebeguParameter = new HashMap<>();
		EbeguParameter paramMaxEinkommen = new EbeguParameter(EbeguParameterKey.PARAM_MASSGEBENDES_EINKOMMEN_MAX, "159000");
		ebeguParameter.put(EbeguParameterKey.PARAM_MASSGEBENDES_EINKOMMEN_MAX, paramMaxEinkommen);
		BetreuungsgutscheinConfigurator configurator = new BetreuungsgutscheinConfigurator();
		List<Rule> rules = configurator.configureRulesForMandant(null, ebeguParameter);
		evaluator = new BetreuungsgutscheinEvaluator(rules);
	}

	@Test
	public void doTestEvaluation(){
		Gesuch testgesuch = createGesuch();
		testgesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(testgesuch);
	}

	private Gesuch createGesuch() {
		Gesuch gesuch = new Gesuch();
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		gesuch.setFamiliensituation(new Familiensituation());
		gesuch.getFamiliensituation().setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		// GS 1
		gesuch.setGesuchsteller1(new Gesuchsteller());
		gesuch.getGesuchsteller1().setFinanzielleSituationContainer(new FinanzielleSituationContainer());
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().setFinanzielleSituationSV(new FinanzielleSituation());
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(erwerbspensumGS1_1.getGueltigAb(), erwerbspensumGS1_1.getGueltigBis(), 50, 0));
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(erwerbspensumGS1_2.getGueltigAb(), erwerbspensumGS1_2.getGueltigBis(), 30, 0));
		// GS 2
		gesuch.setGesuchsteller2(new Gesuchsteller());
		gesuch.getGesuchsteller2().setFinanzielleSituationContainer(new FinanzielleSituationContainer());
		gesuch.getGesuchsteller2().getFinanzielleSituationContainer().setFinanzielleSituationSV(new FinanzielleSituation());
		gesuch.getGesuchsteller2().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(gesuchsperiode.getGueltigAb(), gesuchsperiode.getGueltigBis(), 90, 10));
		return gesuch;
	}

	private Betreuung createBetreuungWithPensum(BetreuungsangebotTyp angebot, int pensum) {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(angebot);
		betreuung.setBetreuungspensumContainers(new HashSet<>());
		BetreuungspensumContainer betreuungspensumContainer = new BetreuungspensumContainer();
		betreuungspensumContainer.setBetreuung(betreuung);
		betreuungspensumContainer.setBetreuungspensumJA(new Betreuungspensum());
		betreuungspensumContainer.getBetreuungspensumJA().setPensum(pensum);
		betreuung.getBetreuungspensumContainers().add(betreuungspensumContainer);
		return betreuung;
	}
}
