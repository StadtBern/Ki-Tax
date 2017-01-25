package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.Constants;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



/**
 * Tests fuer EinkommenAbschnittRule
 */
public class EinkommenAbschnittRuleTest {

	private static final BigDecimal EINKOMMEN_FINANZIELLE_SITUATION = new BigDecimal("100000");
	private static final BigDecimal EINKOMMEN_EKV_ABGELEHNT = new BigDecimal("80000");
	private static final BigDecimal EINKOMMEN_EKV_ANGENOMMEN = new BigDecimal("79990");

	private static BigDecimal MAX_EINKOMMEN = new BigDecimal("159000");
	private final EinkommenAbschnittRule einkommenAbschnittRule = new EinkommenAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);
	private final EinkommenCalcRule einkommenCalcRule = new EinkommenCalcRule(Constants.DEFAULT_GUELTIGKEIT, MAX_EINKOMMEN);


	@Test
	public void testKeineEinkommensverschlechterung() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.calculateFinanzDaten(gesuch);

		List<VerfuegungZeitabschnitt> zeitabschnitte = einkommenAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		zeitabschnitte = einkommenCalcRule.calculate(betreuung, zeitabschnitte);
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(1, zeitabschnitte.size());
		Assert.assertEquals(0, EINKOMMEN_FINANZIELLE_SITUATION.compareTo(zeitabschnitte.get(0).getMassgebendesEinkommen()));
	}

	@Test
	public void testEinkommensverschlechterung2016Abgelehnt() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ABGELEHNT, true);
		TestDataUtil.calculateFinanzDaten(gesuch);

		List<VerfuegungZeitabschnitt> zeitabschnitte = einkommenAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		zeitabschnitte = einkommenCalcRule.calculate(betreuung, zeitabschnitte);
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(2, zeitabschnitte.size());
		Assert.assertEquals(0, EINKOMMEN_FINANZIELLE_SITUATION.compareTo(zeitabschnitte.get(0).getMassgebendesEinkommen()));
		Assert.assertEquals(0, EINKOMMEN_FINANZIELLE_SITUATION.compareTo(zeitabschnitte.get(1).getMassgebendesEinkommen())); // Abgelehnt
	}

	@Test
	public void testEinkommensverschlechterung2016Angenommen() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ANGENOMMEN, true);
		TestDataUtil.calculateFinanzDaten(gesuch);

		List<VerfuegungZeitabschnitt> zeitabschnitte = einkommenAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		zeitabschnitte = einkommenCalcRule.calculate(betreuung, zeitabschnitte);
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(2, zeitabschnitte.size());
		Assert.assertEquals(0, EINKOMMEN_FINANZIELLE_SITUATION.compareTo(zeitabschnitte.get(0).getMassgebendesEinkommen()));
		Assert.assertEquals(0, EINKOMMEN_EKV_ANGENOMMEN.compareTo(zeitabschnitte.get(1).getMassgebendesEinkommen()));
	}

	@Test
	public void testEinkommensverschlechterung2016Abgelehnt2017Angenommen() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ABGELEHNT, true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ANGENOMMEN, false);
		TestDataUtil.calculateFinanzDaten(gesuch);

		List<VerfuegungZeitabschnitt> zeitabschnitte = einkommenAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		zeitabschnitte = einkommenCalcRule.calculate(betreuung, zeitabschnitte);
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(3, zeitabschnitte.size());
		Assert.assertEquals(0, EINKOMMEN_FINANZIELLE_SITUATION.compareTo(zeitabschnitte.get(0).getMassgebendesEinkommen()));
		Assert.assertEquals(0, EINKOMMEN_FINANZIELLE_SITUATION.compareTo(zeitabschnitte.get(1).getMassgebendesEinkommen())); // Abgelehnt
		Assert.assertEquals(0, EINKOMMEN_EKV_ANGENOMMEN.compareTo(zeitabschnitte.get(2).getMassgebendesEinkommen()));
	}

	@Test
	public void testEinkommensverschlechterung2016Angenommen2017Angenommen() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ANGENOMMEN, true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ANGENOMMEN, false);
		TestDataUtil.calculateFinanzDaten(gesuch);

		List<VerfuegungZeitabschnitt> zeitabschnitte = einkommenAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		zeitabschnitte = einkommenCalcRule.calculate(betreuung, zeitabschnitte);
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(3, zeitabschnitte.size());
		Assert.assertEquals(0,EINKOMMEN_FINANZIELLE_SITUATION.compareTo(zeitabschnitte.get(0).getMassgebendesEinkommen()));
		Assert.assertEquals(0,EINKOMMEN_EKV_ANGENOMMEN.compareTo(zeitabschnitte.get(1).getMassgebendesEinkommen()));
		Assert.assertEquals(0,EINKOMMEN_EKV_ANGENOMMEN.compareTo(zeitabschnitte.get(2).getMassgebendesEinkommen()));
	}

	@Test
	public void testEinkommensverschlechterung2016Abgelehnt2017Abgelehnt() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ABGELEHNT, true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), EINKOMMEN_EKV_ABGELEHNT, false);
		TestDataUtil.calculateFinanzDaten(gesuch);

		List<VerfuegungZeitabschnitt> zeitabschnitte = einkommenAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		zeitabschnitte = einkommenCalcRule.calculate(betreuung, zeitabschnitte);
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(3, zeitabschnitte.size());
		Assert.assertEquals(0,EINKOMMEN_FINANZIELLE_SITUATION.compareTo(zeitabschnitte.get(0).getMassgebendesEinkommen()));
		Assert.assertEquals(0,EINKOMMEN_FINANZIELLE_SITUATION.compareTo(zeitabschnitte.get(1).getMassgebendesEinkommen())); // Abgelehnt
		Assert.assertEquals(0,EINKOMMEN_FINANZIELLE_SITUATION.compareTo(zeitabschnitte.get(2).getMassgebendesEinkommen())); // Abgelehnt
	}
}
