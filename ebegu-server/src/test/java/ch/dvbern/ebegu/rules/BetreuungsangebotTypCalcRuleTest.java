package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.Constants;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

/**
 * Testet die BetreuungsangebotTyp Regel
 */
public class BetreuungsangebotTypCalcRuleTest {

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);

	@Test
	public void testAngebotKita() {
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(BetreuungsangebotTyp.KITA));

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testAngebotTagi() {
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(BetreuungsangebotTyp.TAGI));

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(100, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testAngebotTageselternKleinkind() {
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(BetreuungsangebotTyp.TAGESELTERN_KLEINKIND));

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testAngebotTageselternSchulkind() {
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(BetreuungsangebotTyp.TAGESELTERN_SCHULKIND));

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(100, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testAngebotTagesschule() {
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(BetreuungsangebotTyp.TAGESSCHULE));

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(0, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertFalse(result.get(0).getBemerkungen().isEmpty());
	}

	private Betreuung prepareData(BetreuungsangebotTyp betreuungsangebotTyp) {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(betreuungsangebotTyp);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		BetreuungspensumContainer betreuungspensumContainer = new BetreuungspensumContainer();
		betreuungspensumContainer.setBetreuungspensumJA(new Betreuungspensum());
		betreuungspensumContainer.getBetreuungspensumJA().setGueltigkeit(Constants.DEFAULT_GUELTIGKEIT);
		betreuungspensumContainer.getBetreuungspensumJA().setPensum(80);
		betreuung.getBetreuungspensumContainers().add(betreuungspensumContainer);
		return betreuung;
	}
}
