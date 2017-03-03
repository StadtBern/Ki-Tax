package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.MathUtil;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Testet die MaximalesEinkommen-Regel
 */
public class EinkommenCalcRuleTest {

	@Test
	public void testKitaNormalfall() {
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.KITA, 100));

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(0, (new BigDecimal("50000.00")).compareTo(result.get(0).getMassgebendesEinkommen()));
		Assert.assertEquals(100, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertFalse(result.get(0).isBezahltVollkosten());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testKitaEinkommenZuHoch() {
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(MathUtil.DEFAULT.from(180000), BetreuungsangebotTyp.KITA, 100));

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(0,(new BigDecimal("180000.00")).compareTo(result.get(0).getMassgebendesEinkommen()));
		Assert.assertEquals(0, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertFalse(result.get(0).isBezahltVollkosten());
		Assert.assertFalse(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testTagiNormalfall() {
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(MathUtil.DEFAULT.from(50000), BetreuungsangebotTyp.TAGI, 100));

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(0, (new BigDecimal("50000.00")).compareTo(result.get(0).getMassgebendesEinkommen()));
		Assert.assertEquals(100, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertFalse(result.get(0).isBezahltVollkosten());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
	}

	@Test
	public void testTagiEinkommenZuHoch() {
		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(prepareData(MathUtil.DEFAULT.from(180000), BetreuungsangebotTyp.TAGI, 100));

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(0, (new BigDecimal("180000.00")).compareTo(result.get(0).getMassgebendesEinkommen()));
		Assert.assertEquals(100, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertTrue(result.get(0).isBezahltVollkosten());
		Assert.assertFalse(result.get(0).getBemerkungen().isEmpty());
	}

	/**
	 *  Erstellt einen Testfall mit 2 EKV.
	 *  Am Ende schaut es dass die Bemerkungen richtig geschrieben wurden
	 */
	@Test
	public void testAcceptedEKV() {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, BetreuungsangebotTyp.TAGI, 100);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);

		gesuch.setEinkommensverschlechterungInfoContainer(new EinkommensverschlechterungInfoContainer());
		final EinkommensverschlechterungInfo einkommensverschlechterungInfoJA = new EinkommensverschlechterungInfo();
		einkommensverschlechterungInfoJA.setEinkommensverschlechterung(true);
		einkommensverschlechterungInfoJA.setEkvFuerBasisJahrPlus1(true);
		einkommensverschlechterungInfoJA.setEkvFuerBasisJahrPlus2(true);
		einkommensverschlechterungInfoJA.setStichtagFuerBasisJahrPlus1(LocalDate.of(TestDataUtil.PERIODE_JAHR_1, 10, 1));
		einkommensverschlechterungInfoJA.setStichtagFuerBasisJahrPlus2(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, 4, 1));
		gesuch.getEinkommensverschlechterungInfoContainer().setEinkommensverschlechterungInfoJA(einkommensverschlechterungInfoJA);

		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, 100, 0));
		gesuch.getGesuchsteller1().setFinanzielleSituationContainer(new FinanzielleSituationContainer());
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().setFinanzielleSituationJA(new FinanzielleSituation());
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setNettolohn(new BigDecimal(50000));
		TestDataUtil.calculateFinanzDaten(gesuch);

		gesuch.getGesuchsteller1().setEinkommensverschlechterungContainer(new EinkommensverschlechterungContainer());
		final Einkommensverschlechterung ekvJABasisJahrPlus1 = new Einkommensverschlechterung();
		ekvJABasisJahrPlus1.setNettolohnJan(new BigDecimal(25000));
		gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer().setEkvJABasisJahrPlus1(ekvJABasisJahrPlus1);
		final Einkommensverschlechterung ekvJABasisJahrPlus2 = new Einkommensverschlechterung();
		ekvJABasisJahrPlus2.setNettolohnJan(new BigDecimal(20000));
		gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer().setEkvJABasisJahrPlus2(ekvJABasisJahrPlus2);

		List<VerfuegungZeitabschnitt> result = EbeguRuleTestsHelper.calculate(betreuung);

		Assert.assertEquals(3, result.size());
		Assert.assertTrue(result.get(0).getBemerkungen().isEmpty());
		Assert.assertEquals(new BigDecimal(50000), result.get(0).getMassgebendesEinkommen());
		Assert.assertEquals("EINKOMMEN: Ihr massgebendes Einkommen des Jahres " + TestDataUtil.PERIODE_JAHR_1 + " ist gegenüber der Vergleichsperiode um mehr als 20% gesunken. Die Bemessung erfolgt auf dem provisorischen Einkommen des Jahres " + TestDataUtil.PERIODE_JAHR_1 + ".", result.get(1).getBemerkungen());
		Assert.assertEquals(new BigDecimal(25000), result.get(1).getMassgebendesEinkommen());
		Assert.assertEquals("EINKOMMEN: Ihr massgebendes Einkommen des Jahres " + TestDataUtil.PERIODE_JAHR_2 + " ist gegenüber der Vergleichsperiode um mehr als 20% gesunken. Die Bemessung erfolgt auf dem provisorischen Einkommen des Jahres " + TestDataUtil.PERIODE_JAHR_2 + ".", result.get(2).getBemerkungen());
		Assert.assertEquals(new BigDecimal(20000), result.get(2).getMassgebendesEinkommen());
	}

	private Betreuung prepareData(BigDecimal massgebendesEinkommen, BetreuungsangebotTyp angebot, int pensum) {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, angebot, pensum);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, 100, 0));
		gesuch.getGesuchsteller1().setFinanzielleSituationContainer(new FinanzielleSituationContainer());
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().setFinanzielleSituationJA(new FinanzielleSituation());
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setNettolohn(massgebendesEinkommen);
		return betreuung;
	}
}
