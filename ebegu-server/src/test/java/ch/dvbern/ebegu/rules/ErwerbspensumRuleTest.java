package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
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

	private final DateRange defaultGueltigkeit = new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME);
	private final ErwerbspensumRule erwerbspensumRule = new ErwerbspensumRule(defaultGueltigkeit);

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);


	@Test
	public void testKeinErwerbspensum() {
		BetreuungspensumContainer betreuungspensumContainer = TestDataUtil.createGesuchWithBetreuungspensumContainer(true);

		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuungspensumContainer, new ArrayList<>(), new FinanzielleSituationResultateDTO(betreuungspensumContainer.extractGesuch(), 4, new BigDecimal("10000")));
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testNormalfallZweiGesuchsteller() {
		BetreuungspensumContainer betreuungspensumContainer = TestDataUtil.createGesuchWithBetreuungspensumContainer(true);
		Gesuch gesuch = betreuungspensumContainer.extractGesuch();

		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 100, 0));
		gesuch.getGesuchsteller2().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 40, 0));

		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuungspensumContainer, new ArrayList<>(), new FinanzielleSituationResultateDTO(betreuungspensumContainer.extractGesuch(), 4, new BigDecimal("10000")));
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(40, result.get(0).getAnspruchspensumOriginal());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testNormalfallEinGesuchsteller() {
		BetreuungspensumContainer betreuungspensumContainer = TestDataUtil.createGesuchWithBetreuungspensumContainer(false);
		Gesuch gesuch = betreuungspensumContainer.extractGesuch();

		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));

		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuungspensumContainer, new ArrayList<>(), new FinanzielleSituationResultateDTO(betreuungspensumContainer.extractGesuch(), 4, new BigDecimal("10000")));
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(60, result.get(0).getAnspruchspensumOriginal());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testNurEinErwerbspensumBeiZweiGesuchstellern() throws Exception {
		BetreuungspensumContainer betreuungspensumContainer = TestDataUtil.createGesuchWithBetreuungspensumContainer(true);
		Gesuch gesuch = betreuungspensumContainer.extractGesuch();

		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 80, 0));

		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuungspensumContainer, new ArrayList<>(), new FinanzielleSituationResultateDTO(betreuungspensumContainer.extractGesuch(), 4, new BigDecimal("10000")));
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(0, result.get(0).getAnspruchspensumOriginal());
		Assert.assertFalse(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testMehrAls100ProzentBeiEinemGesuchsteller() {
		BetreuungspensumContainer betreuungspensumContainer = TestDataUtil.createGesuchWithBetreuungspensumContainer(false);
		Gesuch gesuch = betreuungspensumContainer.extractGesuch();

		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 100, 10));

		List<VerfuegungZeitabschnitt> result = erwerbspensumRule.calculate(betreuungspensumContainer, new ArrayList<>(), new FinanzielleSituationResultateDTO(betreuungspensumContainer.extractGesuch(), 4, new BigDecimal("10000")));
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(100, result.get(0).getAnspruchspensumOriginal());
		Assert.assertFalse(result.get(0).getBemerkungen().isEmpty());
	}
}
