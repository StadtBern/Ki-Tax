package ch.dvbern.ebegu.tests.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import org.junit.Test;

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

	private Gesuch createGesuch() {
		Gesuch gesuch = new Gesuch();
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		TestDataUtil.calculateFinanzDaten(gesuch);
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
		// Kind 1
		Betreuung betreuungKind1 = createBetreuungWithPensum(gesuch, BetreuungsangebotTyp.KITA, gesuchsperiode, 60);
		betreuungKind1.getKind().getKindJA().setPensumFachstelle(new PensumFachstelle());
		betreuungKind1.getKind().getKindJA().getPensumFachstelle().setGueltigkeit(fachstelleGueltigkeit);
		betreuungKind1.getKind().getKindJA().getPensumFachstelle().setPensum(80);
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
