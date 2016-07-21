package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.MathUtil;
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
public class MaximalesEinkommenCalcRuleTest {

	private BigDecimal MAX_EINKOMMEN = new BigDecimal("159000");
	private final DateRange defaultGueltigkeit = new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME);
	private final MaximalesEinkommenAbschnittRule maximalesEinkommenAbschnittRule = new MaximalesEinkommenAbschnittRule(defaultGueltigkeit);
	private final MaximalesEinkommenCalcRule maximalesEinkommenCalcRule = new MaximalesEinkommenCalcRule(defaultGueltigkeit, MAX_EINKOMMEN);
	private final ErwerbspensumRule erwerbspensumRule = new ErwerbspensumRule(defaultGueltigkeit);

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);

	@Test
	public void testNormalfall() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);

		List<VerfuegungZeitabschnitt> zeitabschnitteAusGrundregeln = prepareData(betreuung, MathUtil.DEFAULT.from(50000));
		List<VerfuegungZeitabschnitt> neueAbschnitte  = maximalesEinkommenAbschnittRule.calculate(betreuung, zeitabschnitteAusGrundregeln);
		List<VerfuegungZeitabschnitt> result = maximalesEinkommenCalcRule.calculate(betreuung, neueAbschnitte);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(new BigDecimal("50000.00"), result.get(0).getMassgebendesEinkommen());
		Assert.assertEquals(100, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testEinkommenZuHoch() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);

		List<VerfuegungZeitabschnitt> zeitabschnitteAusGrundregeln = prepareData(betreuung, MathUtil.DEFAULT.from(180000));
		List<VerfuegungZeitabschnitt> neueAbschnitte  = maximalesEinkommenAbschnittRule.calculate(betreuung, zeitabschnitteAusGrundregeln);
		List<VerfuegungZeitabschnitt> result = maximalesEinkommenCalcRule.calculate(betreuung, neueAbschnitte);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(new BigDecimal("180000.00"), result.get(0).getMassgebendesEinkommen());
		Assert.assertEquals(0, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertFalse(result.get(0).getBemerkungen().isEmpty());
	}

	private List<VerfuegungZeitabschnitt> prepareData(Betreuung betreuung, BigDecimal massgebendesEinkommen) {
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.getFinanzDatenDTO().setMassgebendesEinkommenBasisjahr(massgebendesEinkommen);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 100, 0));
		return erwerbspensumRule.calculate(betreuung, new ArrayList<>());
	}
}
