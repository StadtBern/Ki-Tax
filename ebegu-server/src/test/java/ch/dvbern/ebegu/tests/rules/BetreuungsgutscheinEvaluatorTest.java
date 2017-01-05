package ch.dvbern.ebegu.tests.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.Kinderabzug;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.rules.RuleKey;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.MathUtil;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;

import static ch.dvbern.ebegu.tets.TestDataUtil.createDefaultInstitutionStammdaten;

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
public class BetreuungsgutscheinEvaluatorTest extends AbstractBGRechnerTest {


	private DateRange erwerbspensumGS1_1 = new DateRange(LocalDate.of(2010, Month.FEBRUARY, 2), LocalDate.of(2017, Month.MARCH, 20));
	private DateRange erwerbspensumGS1_2 = new DateRange(LocalDate.of(2017, Month.JANUARY, 1), LocalDate.of(2017, Month.JULY, 31));

	private DateRange fachstelleGueltigkeit = new DateRange(LocalDate.of(2016, Month.AUGUST, 1), LocalDate.of(2017, Month.APRIL, 30));

	private DateRange gesuchsperiode = new DateRange(LocalDate.of(2016, Month.AUGUST, 1), LocalDate.of(2017, Month.JULY, 31));


	@Test
	public void doTestEvaluation(){
		Gesuch testgesuch = createGesuch();
		testgesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(testgesuch, getParameter());
		for (KindContainer kindContainer : testgesuch.getKindContainers()) {
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				System.out.println(betreuung.getVerfuegung());
			}
		}
	}

	@Test
	public void doTestEvaluationGeneratedBemerkungen(){
		Gesuch testgesuch = createGesuch();
		testgesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		testgesuch.getFinanzDatenDTO().setMassgebendesEinkBjVorAbzFamGr(new BigDecimal("500000")); //zu hoch -> Comment wird erzeugt

		evaluator.evaluate(testgesuch, getParameter());

		for (KindContainer kindContainer : testgesuch.getKindContainers()) {
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				Assert.assertFalse(betreuung.getVerfuegung().getGeneratedBemerkungen().isEmpty());
				Assert.assertTrue(betreuung.getVerfuegung().getGeneratedBemerkungen().contains(RuleKey.EINKOMMEN.name()));
			}
		}
	}

	@Test
	public void doTestEvaluationForFamiliensituation() {
		Gesuch testgesuch = createGesuch();
		testgesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		Verfuegung verfuegung = evaluator.evaluateFamiliensituation(testgesuch);

		Assert.assertNotNull(verfuegung);
		Assert.assertEquals(12, verfuegung.getZeitabschnitte().size());

		Assert.assertEquals(MathUtil.EINE_NACHKOMMASTELLE.from(3d), verfuegung.getZeitabschnitte().get(0).getFamGroesse());
		Assert.assertEquals(0, new BigDecimal("20000").compareTo(verfuegung.getZeitabschnitte().get(0).getMassgebendesEinkommenVorAbzFamgr()));
		Assert.assertEquals(0, new BigDecimal("11280").compareTo(verfuegung.getZeitabschnitte().get(0).getAbzugFamGroesse()));
		Assert.assertEquals(0, new BigDecimal("8720").compareTo(verfuegung.getZeitabschnitte().get(0).getMassgebendesEinkommen()));
	}


	private Gesuch createGesuch() {
		Gesuch gesuch = new Gesuch();
		final Fall fall = TestDataUtil.createDefaultFall();
		fall.setFallNummer(2);
		gesuch.setFall(fall);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.initFamiliensituationContainer();
		gesuch.extractFamiliensituation().setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		// GS 1
		GesuchstellerContainer gsContainer1 = new GesuchstellerContainer();
		gsContainer1.setGesuchstellerJA(new Gesuchsteller());
		gesuch.setGesuchsteller1(gsContainer1);
		gesuch.getGesuchsteller1().setFinanzielleSituationContainer(new FinanzielleSituationContainer());
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().setFinanzielleSituationJA(new FinanzielleSituation());
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setNettolohn(MathUtil.DEFAULT.from(20000));
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(erwerbspensumGS1_1.getGueltigAb(), erwerbspensumGS1_1.getGueltigBis(), 50, 0));
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(erwerbspensumGS1_2.getGueltigAb(), erwerbspensumGS1_2.getGueltigBis(), 30, 0));
		// GS 2
		GesuchstellerContainer gsContainer2 = new GesuchstellerContainer();
		gsContainer2.setGesuchstellerJA(new Gesuchsteller());
		gesuch.setGesuchsteller2(gsContainer2);
		gesuch.getGesuchsteller2().setFinanzielleSituationContainer(new FinanzielleSituationContainer());
		gesuch.getGesuchsteller2().getFinanzielleSituationContainer().setFinanzielleSituationJA(new FinanzielleSituation());
		gesuch.getGesuchsteller2().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(gesuchsperiode.getGueltigAb(), gesuchsperiode.getGueltigBis(), 90, 10));
		// Adressen
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		// Kind 1
		Betreuung betreuungKind1 = createBetreuungWithPensum(gesuch, BetreuungsangebotTyp.KITA, gesuchsperiode, 60);
		betreuungKind1.getKind().getKindJA().setPensumFachstelle(new PensumFachstelle());
		betreuungKind1.getKind().getKindJA().getPensumFachstelle().setGueltigkeit(fachstelleGueltigkeit);
		betreuungKind1.getKind().getKindJA().getPensumFachstelle().setPensum(80);
		TestDataUtil.calculateFinanzDaten(gesuch);
		return gesuch;
	}


	private Betreuung createBetreuungWithPensum(Gesuch gesuch, BetreuungsangebotTyp angebot, DateRange gueltigkeit, int pensum) {
		Betreuung betreuung = new Betreuung();
		KindContainer kindContainer = new KindContainer();
		betreuung.setKind(kindContainer);
		kindContainer.getBetreuungen().add(betreuung);
		betreuung.getKind().setKindJA(new Kind());
		betreuung.getKind().setGesuch(gesuch);
		betreuung.getKind().getKindJA().setGeburtsdatum(LocalDate.now().minusYears(4));
		betreuung.getKind().getKindJA().setWohnhaftImGleichenHaushalt(100);
		betreuung.getKind().getKindJA().setKinderabzug(Kinderabzug.GANZER_ABZUG);
		gesuch.getKindContainers().add(betreuung.getKind());
		betreuung.setInstitutionStammdaten(createDefaultInstitutionStammdaten());
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(angebot);
		betreuung.setBetreuungspensumContainers(new HashSet<>());
		BetreuungspensumContainer betreuungspensumContainer = new BetreuungspensumContainer();
		betreuungspensumContainer.setBetreuung(betreuung);
		betreuungspensumContainer.setBetreuungspensumJA(new Betreuungspensum());
		betreuungspensumContainer.getBetreuungspensumJA().setGueltigkeit(gueltigkeit);
		betreuungspensumContainer.getBetreuungspensumJA().setPensum(pensum);
		betreuung.getBetreuungspensumContainers().add(betreuungspensumContainer);
		return betreuung;
	}
}
