package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/**
 * Testet die BetreuungsangebotTyp Regel
 */
public class BetreuungsangebotTypRuleTest {

	private final DateRange defaultGueltigkeit = new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME);
	private final BetreuungsangebotTypRule betreuungsangebotTypRule = new BetreuungsangebotTypRule(defaultGueltigkeit);
	private final ErwerbspensumRule erwerbspensumRule = new ErwerbspensumRule(defaultGueltigkeit);

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);

	@Test
	public void testAngebotKita() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);

		List<VerfuegungZeitabschnitt> zeitabschnitteAusGrundregeln = prepareData(BetreuungsangebotTyp.KITA, betreuung);

		List<VerfuegungZeitabschnitt> result = betreuungsangebotTypRule.calculate(betreuung, zeitabschnitteAusGrundregeln);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(100, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testAngebotTagi() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);

		List<VerfuegungZeitabschnitt> zeitabschnitteAusGrundregeln = prepareData(BetreuungsangebotTyp.TAGI, betreuung);

		List<VerfuegungZeitabschnitt> result = betreuungsangebotTypRule.calculate(betreuung, zeitabschnitteAusGrundregeln);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(100, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testAngebotTageseltern() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);

		List<VerfuegungZeitabschnitt> zeitabschnitteAusGrundregeln = prepareData(BetreuungsangebotTyp.TAGESELTERN_KLEINKIND, betreuung);

		List<VerfuegungZeitabschnitt> result = betreuungsangebotTypRule.calculate(betreuung, zeitabschnitteAusGrundregeln);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(100, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testAngebotTagesschule() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);

		List<VerfuegungZeitabschnitt> zeitabschnitteAusGrundregeln = prepareData(BetreuungsangebotTyp.TAGESSCHULE, betreuung);

		List<VerfuegungZeitabschnitt> result = betreuungsangebotTypRule.calculate(betreuung, zeitabschnitteAusGrundregeln);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(0, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertFalse(result.get(0).getBemerkungen().isEmpty());
	}

	private List<VerfuegungZeitabschnitt> prepareData(BetreuungsangebotTyp betreuungsangebotTyp, Betreuung betreuung) {
		Gesuch gesuch = betreuung.extractGesuch();
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(betreuungsangebotTyp);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 100, 0));
		return erwerbspensumRule.calculate(betreuung, new ArrayList<>());
	}
}
