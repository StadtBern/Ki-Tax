package ch.dvbern.ebegu.tests.util;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.services.EbeguParameterService;
import ch.dvbern.ebegu.tests.AbstractEbeguLoginTest;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.FinanzielleSituationRechner;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

/**
 * Tests fuer FinanzielleSituationRechner
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class FinanzielleSituationRechnerTest extends AbstractEbeguLoginTest {

	private static final BigDecimal EINKOMMEN_FINANZIELLE_SITUATION = new BigDecimal("100000");
	private static final BigDecimal EINKOMMEN_EKV_ABGELEHNT = new BigDecimal("80000");
	private static final BigDecimal EINKOMMEN_EKV_ANGENOMMEN = new BigDecimal("79990");
	private static final BigDecimal EINKOMMEN_EKV_ANGENOMMEN_2 = new BigDecimal("79000");

	@Inject
	private EbeguParameterService ebeguParameterService;

	@Inject
	private FinanzielleSituationRechner finSitRechner;



	@Test
	public void testPositiverDurschnittlicherGewinn() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		//positiv value
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setGeschaeftsgewinnBasisjahr(BigDecimal.valueOf(100));
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setGeschaeftsgewinnBasisjahrMinus1(BigDecimal.valueOf(-100));
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setGeschaeftsgewinnBasisjahrMinus2(BigDecimal.valueOf(300));
		FinanzielleSituationResultateDTO finSitResultateDTO1 = finSitRechner.calculateResultateFinanzielleSituation(gesuch, gesuch.extractFamiliensituation().hasSecondGesuchsteller());

		Assert.assertEquals(BigDecimal.valueOf(100), finSitResultateDTO1.getGeschaeftsgewinnDurchschnittGesuchsteller1());
	}

	@Test
	public void testNegativerDurschnittlicherGewinn() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);

		//negativ value
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setGeschaeftsgewinnBasisjahr(BigDecimal.valueOf(-100));
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setGeschaeftsgewinnBasisjahrMinus1(BigDecimal.valueOf(-100));
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setGeschaeftsgewinnBasisjahrMinus2(BigDecimal.valueOf(-300));
		FinanzielleSituationResultateDTO finSitResultateDTO2 = finSitRechner.calculateResultateFinanzielleSituation(gesuch, gesuch.extractFamiliensituation().hasSecondGesuchsteller());

		Assert.assertEquals(BigDecimal.ZERO, finSitResultateDTO2.getGeschaeftsgewinnDurchschnittGesuchsteller1());
	}


	@Test
	public void testKeineEinkommensverschlechterung() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjVorAbzFamGr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(BigDecimal.ZERO, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP1VorAbzFamGr());
		Assert.assertNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());

		Assert.assertEquals(BigDecimal.ZERO, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP2VorAbzFamGr());
		Assert.assertNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
	}

	@Test
	public void testEinkommensverschlechterung2016Abgelehnt() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ABGELEHNT, true);
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjVorAbzFamGr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP1VorAbzFamGr()); // Abgelehnt
		Assert.assertNotNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());
		Assert.assertFalse(gesuch.getFinanzDatenDTO().isEkv1Accepted());

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP2VorAbzFamGr());
		Assert.assertNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
	}

	@Test
	public void testEinkommensverschlechterung2016Angenommen() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ANGENOMMEN, true);
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjVorAbzFamGr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(EINKOMMEN_EKV_ANGENOMMEN, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP1VorAbzFamGr());
		Assert.assertEquals(TestDataUtil.STICHTAG_EKV_1_GUELTIG, gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());
		Assert.assertTrue(gesuch.getFinanzDatenDTO().isEkv1Accepted());

		Assert.assertEquals(EINKOMMEN_EKV_ANGENOMMEN, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP2VorAbzFamGr());
		Assert.assertNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
	}

	@Test
	public void testEinkommensverschlechterung2016Abgelehnt2017Angenommen() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ABGELEHNT, true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ANGENOMMEN, false);
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjVorAbzFamGr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP1VorAbzFamGr()); // Abgelehnt
		Assert.assertNotNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());
		Assert.assertFalse(gesuch.getFinanzDatenDTO().isEkv1Accepted());

		Assert.assertEquals(EINKOMMEN_EKV_ANGENOMMEN, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP2VorAbzFamGr());
		Assert.assertEquals(TestDataUtil.STICHTAG_EKV_2_GUELTIG, gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
	}

	@Test
	public void testEinkommensverschlechterung2016Angenommen2017Abgelehnt() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ANGENOMMEN, true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ANGENOMMEN, false); // nicht weniger als im vorherigen Jahr
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjVorAbzFamGr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(EINKOMMEN_EKV_ANGENOMMEN, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP1VorAbzFamGr());
		Assert.assertEquals(TestDataUtil.STICHTAG_EKV_1_GUELTIG, gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());
		Assert.assertTrue(gesuch.getFinanzDatenDTO().isEkv1Accepted());

		Assert.assertEquals(EINKOMMEN_EKV_ANGENOMMEN, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP2VorAbzFamGr());
		Assert.assertEquals(TestDataUtil.STICHTAG_EKV_2_GUELTIG, gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
		Assert.assertFalse(gesuch.getFinanzDatenDTO().isEkv2Accepted());
	}

	@Test
	public void testEinkommensverschlechterung2016Angenommen2017Angenommen() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ANGENOMMEN, true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ANGENOMMEN_2, false);
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjVorAbzFamGr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(EINKOMMEN_EKV_ANGENOMMEN, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP1VorAbzFamGr());
		Assert.assertEquals(TestDataUtil.STICHTAG_EKV_1_GUELTIG, gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());
		Assert.assertTrue(gesuch.getFinanzDatenDTO().isEkv1Accepted());

		Assert.assertEquals(EINKOMMEN_EKV_ANGENOMMEN_2, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP2VorAbzFamGr());
		Assert.assertEquals(TestDataUtil.STICHTAG_EKV_2_GUELTIG, gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
		Assert.assertTrue(gesuch.getFinanzDatenDTO().isEkv2Accepted());
	}

	@Test
	public void testEinkommensverschlechterung2016Abgelehnt2017Abgelehnt() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ABGELEHNT, true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ABGELEHNT, false);
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjVorAbzFamGr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP1VorAbzFamGr()); // Abgelehnt
		Assert.assertNotNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());
		Assert.assertFalse(gesuch.getFinanzDatenDTO().isEkv1Accepted());

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP2VorAbzFamGr()); // Abgelehnt
		Assert.assertNotNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
		Assert.assertFalse(gesuch.getFinanzDatenDTO().isEkv2Accepted());
	}

	@Test
	public void testEinkommensverschlechterungDezember2016Angenommen() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ANGENOMMEN, true);
		gesuch.getEinkommensverschlechterungInfoContainer().getEinkommensverschlechterungInfoJA().setStichtagFuerBasisJahrPlus1(LocalDate.of(2016, Month.DECEMBER, 1));
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjVorAbzFamGr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(EINKOMMEN_EKV_ANGENOMMEN, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP1VorAbzFamGr());
		Assert.assertEquals(LocalDate.of(2017, Month.JANUARY, 1), gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());
		Assert.assertTrue(gesuch.getFinanzDatenDTO().isEkv1Accepted());

		Assert.assertEquals(EINKOMMEN_EKV_ANGENOMMEN, gesuch.getFinanzDatenDTO().getMassgebendesEinkBjP2VorAbzFamGr());
		Assert.assertNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
	}

}
