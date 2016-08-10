package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashSet;
import java.util.List;

import static ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator.createInitialenRestanspruch;

/**
 * Tests für Betreuungspensum-Regel
 */
public class BetreuungspensumRuleTest {

	private final ErwerbspensumRule erwerbspensumRule = new ErwerbspensumRule(Constants.DEFAULT_GUELTIGKEIT);
	private final FachstelleDataRule fachstelleDataRule = new FachstelleDataRule(Constants.DEFAULT_GUELTIGKEIT);
	private final BetreuungspensumAbschnittRule betreuungspensumAbschnittRule = new BetreuungspensumAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private final BetreuungspensumCalcRule betreuungspensumCalcRule = new BetreuungspensumCalcRule(Constants.DEFAULT_GUELTIGKEIT);
	private final RestanspruchEvaluator restanspruchEvaluator = new RestanspruchEvaluator(Constants.DEFAULT_GUELTIGKEIT);

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);

	@Test
	public void testKitaNormalfall() {
		Betreuung betreuung = createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE,  BetreuungsangebotTyp.KITA, 60);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, createInitialenRestanspruch(betreuung.extractGesuchsperiode()));
		result = betreuungspensumAbschnittRule.calculate(betreuung, result);
		result = betreuungspensumCalcRule.calculate(betreuung, result);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(60, result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, result.get(0).getAnspruchspensumRest());
	}

	@Test
	public void testKitaZuwenigAnspruch() {
		Betreuung betreuung = createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 80);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, createInitialenRestanspruch(betreuung.extractGesuchsperiode()));
		result = betreuungspensumAbschnittRule.calculate(betreuung, result);
		result = betreuungspensumCalcRule.calculate(betreuung, result);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(60, result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(80, result.get(0).getBetreuungspensum());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, result.get(0).getAnspruchspensumRest());
	}

	@Test
	public void testKitaMitRestanspruch() {
		Betreuung betreuung = createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 60);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 80, 0));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, createInitialenRestanspruch(betreuung.extractGesuchsperiode()));
		result = betreuungspensumAbschnittRule.calculate(betreuung, result);
		result = betreuungspensumCalcRule.calculate(betreuung, result);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(80, result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(20, result.get(0).getAnspruchspensumRest());
	}

	@Test
	public void testZweiKitas() {
		Betreuung betreuung1 = createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 60);
		Betreuung betreuung2 = createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 40);
		betreuung1.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 80, 0));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung1, createInitialenRestanspruch(betreuung1.extractGesuchsperiode()));
		// Nach Kita 1: 20% Rest
		result = betreuungspensumAbschnittRule.calculate(betreuung1, result);
		result = betreuungspensumCalcRule.calculate(betreuung1, result);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(80, result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(20, result.get(0).getAnspruchspensumRest());
		// Anspruchsrest berechnen
		result = restanspruchEvaluator.createVerfuegungsZeitabschnitte(betreuung1, result);
		// Kita 2: Reicht nicht mehr ganz
		betreuung2.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 80, 0));
		result = erwerbspensumRule.calculate(betreuung2, result);
		result = betreuungspensumAbschnittRule.calculate(betreuung2, result);
		result = betreuungspensumCalcRule.calculate(betreuung2, result);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(80, result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(40, result.get(0).getBetreuungspensum());
		Assert.assertEquals(20, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, result.get(0).getAnspruchspensumRest());
	}

	@Test
	public void testTageselternSchulkinderOhneErwerbspensum() {
		// Tageseltern Schulkinder erhalten immer soviel wie sie wollen, unabhängig von Erwerbspensum
		Betreuung betreuung = createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.TAGESELTERN_SCHULKIND, 80);
		List<VerfuegungZeitabschnitt> result = betreuungspensumAbschnittRule.calculate(betreuung, createInitialenRestanspruch(betreuung.extractGesuchsperiode()));
		result = betreuungspensumCalcRule.calculate(betreuung, result);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(0, result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(80, result.get(0).getBetreuungspensum());
		Assert.assertEquals(80, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
	}

	@Test
	public void testTageselternSchulkinderMitTiefemErwerbspensum() {
		// Tageseltern Schulkinder erhalten immer soviel wie sie wollen, unabhängig von Erwerbspensum
		Betreuung betreuung = createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.TAGESELTERN_SCHULKIND, 80);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, createInitialenRestanspruch(betreuung.extractGesuchsperiode()));
		result = betreuungspensumAbschnittRule.calculate(betreuung, result);
		result = betreuungspensumCalcRule.calculate(betreuung, result);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(60, result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(80, result.get(0).getBetreuungspensum());
		Assert.assertEquals(80, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, result.get(0).getAnspruchspensumRest());
	}

	@Test
	public void testKitaMitFachstelleWenigerAlsPensum() {
		Betreuung betreuung = createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 60);
		betreuung.getKind().getKindJA().setPensumFachstelle(new PensumFachstelle());
		betreuung.getKind().getKindJA().getPensumFachstelle().setPensum(40);
		betreuung.getKind().getKindJA().getPensumFachstelle().setGueltigkeit(new DateRange(START_PERIODE, ENDE_PERIODE));
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 80, 0));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, createInitialenRestanspruch(betreuung.extractGesuchsperiode()));
		result = fachstelleDataRule.calculate(betreuung, result);
		result = betreuungspensumAbschnittRule.calculate(betreuung, result);
		result = betreuungspensumCalcRule.calculate(betreuung, result);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(80, result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(40, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, result.get(0).getAnspruchspensumRest());
	}

	@Test
	public void testKitaMitFachstelleUndRestPensum() {
		Betreuung betreuung = createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE, BetreuungsangebotTyp.KITA, 60);
		betreuung.getKind().getKindJA().setPensumFachstelle(new PensumFachstelle());
		betreuung.getKind().getKindJA().getPensumFachstelle().setPensum(80);
		betreuung.getKind().getKindJA().getPensumFachstelle().setGueltigkeit(new DateRange(START_PERIODE, ENDE_PERIODE));
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 40, 0));
		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, createInitialenRestanspruch(betreuung.extractGesuchsperiode()));
		result = fachstelleDataRule.calculate(betreuung, result);
		result = betreuungspensumAbschnittRule.calculate(betreuung, result);
		result = betreuungspensumCalcRule.calculate(betreuung, result);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(40, result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(80, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(20, result.get(0).getAnspruchspensumRest());
	}

	private Betreuung createBetreuungWithPensum(LocalDate von, LocalDate bis, BetreuungsangebotTyp angebot, int pensum) {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(angebot);
		betreuung.setBetreuungspensumContainers(new LinkedHashSet<>());
		BetreuungspensumContainer betreuungspensumContainer = new BetreuungspensumContainer();
		betreuungspensumContainer.setBetreuung(betreuung);
		DateRange gueltigkeit = new DateRange(von, bis);
		betreuungspensumContainer.setBetreuungspensumJA(new Betreuungspensum(gueltigkeit));
		betreuungspensumContainer.getBetreuungspensumJA().setPensum(pensum);
		betreuung.getBetreuungspensumContainers().add(betreuungspensumContainer);
		return betreuung;
	}
}
