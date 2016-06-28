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
 * Testet die MaximalesEinkommen-Regel
 */
public class MaximalesEinkommenRuleTest {

	private BigDecimal MAX_EINKOMMEN = new BigDecimal("159000");
	private final DateRange defaultGueltigkeit = new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME);
	private final MaximalesEinkommenRule maximalesEinkommenRule = new MaximalesEinkommenRule(defaultGueltigkeit, MAX_EINKOMMEN);
	private final ErwerbspensumRule erwerbspensumRule = new ErwerbspensumRule(defaultGueltigkeit);

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);

	@Test
	public void testNormalfall() {
		BetreuungspensumContainer betreuungspensumContainer = TestDataUtil.createGesuchWithBetreuungspensumContainer(false);
		FinanzielleSituationResultateDTO dto = new FinanzielleSituationResultateDTO(betreuungspensumContainer.extractGesuch(), 4, new BigDecimal("10000"));
		dto.setMassgebendesEinkommen(new BigDecimal("50000"));

		List<VerfuegungZeitabschnitt> zeitabschnitteAusGrundregeln = prepareData(betreuungspensumContainer, dto);

		List<VerfuegungZeitabschnitt> result = maximalesEinkommenRule.calculate(betreuungspensumContainer, zeitabschnitteAusGrundregeln, dto);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(new BigDecimal("50000"), result.get(0).getMassgebendesEinkommen());
		Assert.assertEquals(100, result.get(0).getAnspruchspensumOriginal());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testEinkommenZuHoch() {
		BetreuungspensumContainer betreuungspensumContainer = TestDataUtil.createGesuchWithBetreuungspensumContainer(false);
		FinanzielleSituationResultateDTO dto = new FinanzielleSituationResultateDTO(betreuungspensumContainer.extractGesuch(), 4, new BigDecimal("10000"));
		dto.setMassgebendesEinkommen(new BigDecimal("180000"));

		List<VerfuegungZeitabschnitt> zeitabschnitteAusGrundregeln = prepareData(betreuungspensumContainer, dto);

		List<VerfuegungZeitabschnitt> result = maximalesEinkommenRule.calculate(betreuungspensumContainer, zeitabschnitteAusGrundregeln, dto);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(new BigDecimal("180000"), result.get(0).getMassgebendesEinkommen());
		Assert.assertEquals(0, result.get(0).getAnspruchspensumOriginal());
		Assert.assertFalse(result.get(0).getBemerkungen().isEmpty());
	}

	private List<VerfuegungZeitabschnitt> prepareData(BetreuungspensumContainer betreuungspensumContainer, FinanzielleSituationResultateDTO dto) {
		Gesuch gesuch = betreuungspensumContainer.extractGesuch();
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 100, 0));
		return erwerbspensumRule.calculate(betreuungspensumContainer, new ArrayList<>(), dto);
	}
}
