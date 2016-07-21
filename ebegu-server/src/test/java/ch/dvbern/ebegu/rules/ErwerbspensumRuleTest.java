package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.Constants;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests für ErwerbspensumRule
 */
public class ErwerbspensumRuleTest {

	private final ErwerbspensumRule erwerbspensumRule = new ErwerbspensumRule(Constants.DEFAULT_GUELTIGKEIT);

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);


	@Test
	public void testKeinErwerbspensum() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);

		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, new ArrayList<>(), new FinanzielleSituationResultateDTO(betreuung.extractGesuch(), 4, new BigDecimal("10000")));
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testNormalfallZweiGesuchsteller() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();

		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 100, 0));
		gesuch.getGesuchsteller2().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 40, 0));

		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, new ArrayList<>(), new FinanzielleSituationResultateDTO(betreuung.extractGesuch(), 4, new BigDecimal("10000")));
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(40, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testNormalfallEinGesuchsteller() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();

		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));

		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, new ArrayList<>(), new FinanzielleSituationResultateDTO(betreuung.extractGesuch(), 4, new BigDecimal("10000")));
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testNurEinErwerbspensumBeiZweiGesuchstellern() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();

		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 80, 0));

		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, new ArrayList<>(), new FinanzielleSituationResultateDTO(betreuung.extractGesuch(), 4, new BigDecimal("10000")));
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(0, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertFalse(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testMehrAls100ProzentBeiEinemGesuchsteller() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();

		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 100, 10));

		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuung, new ArrayList<>(), new FinanzielleSituationResultateDTO(betreuung.extractGesuch(), 4, new BigDecimal("10000")));
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(100, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertFalse(result.get(0).getBemerkungen().isEmpty());
	}
}
