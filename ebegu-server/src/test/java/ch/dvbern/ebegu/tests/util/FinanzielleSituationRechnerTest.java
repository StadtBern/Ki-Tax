package ch.dvbern.ebegu.tests.util;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.enums.Kinderabzug;
import ch.dvbern.ebegu.services.EbeguParameterService;
import ch.dvbern.ebegu.tests.AbstractEbeguTest;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.FinanzielleSituationRechner;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Tests fuer FinanzielleSituationRechner
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class FinanzielleSituationRechnerTest extends AbstractEbeguTest {

	private static final BigDecimal EINKOMMEN_FINANZIELLE_SITUATION = new BigDecimal("100000");
	private static final BigDecimal EINKOMMEN_EKV_ABGELEHNT = new BigDecimal("80001");
	private static final BigDecimal EINKOMMEN_EKV_ANGENOMMEN = new BigDecimal("80000");
	private static final BigDecimal EINKOMMEN_EKV2_ANGENOMMEN = new BigDecimal("50000");
	private static final BigDecimal EINKOMMEN_EKV2_ABGELEHNT = new BigDecimal("64001");
	private static final double DELTA = 1e-15;
	public static final LocalDate DATE_2005 = LocalDate.of(2005, 12, 31);

	@Inject
	private EbeguParameterService ebeguParameterService;

	@Inject
	private FinanzielleSituationRechner finSitRechner;


	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return AbstractEbeguTest.createTestArchive();
	}



	@Test
	public void testPositiverDurschnittlicherGewinn() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		//positiv value
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setGeschaeftsgewinnBasisjahr(BigDecimal.valueOf(100));
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setGeschaeftsgewinnBasisjahrMinus1(BigDecimal.valueOf(-100));
		gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationJA().setGeschaeftsgewinnBasisjahrMinus2(BigDecimal.valueOf(300));
		FinanzielleSituationResultateDTO finSitResultateDTO1 = finSitRechner.calculateResultateFinanzielleSituation(gesuch);

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
		FinanzielleSituationResultateDTO finSitResultateDTO2 = finSitRechner.calculateResultateFinanzielleSituation(gesuch);

		Assert.assertEquals(BigDecimal.ZERO, finSitResultateDTO2.getGeschaeftsgewinnDurchschnittGesuchsteller1());
	}


	@Test
	public void testKeineEinkommensverschlechterung() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(BigDecimal.ZERO, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahrPlus1());
		Assert.assertNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());

		Assert.assertEquals(BigDecimal.ZERO, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahrPlus2());
		Assert.assertNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
	}

	@Test
	public void testEinkommensverschlechterung2016Abgelehnt() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, EINKOMMEN_EKV_ABGELEHNT, true);
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(BigDecimal.ZERO, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahrPlus1());
		Assert.assertNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());

		Assert.assertEquals(BigDecimal.ZERO, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahrPlus2());
		Assert.assertNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
	}

	@Test
	public void testEinkommensverschlechterung2016Angenommen() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, EINKOMMEN_EKV_ANGENOMMEN, true);
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(EINKOMMEN_EKV_ANGENOMMEN, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahrPlus1());
		Assert.assertEquals(TestDataUtil.STICHTAG_EKV_1, gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());

		Assert.assertEquals(BigDecimal.ZERO, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahrPlus2());
		Assert.assertNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
	}

	@Test
	public void testEinkommensverschlechterung2016Abgelehnt2017Angenommen() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, EINKOMMEN_EKV_ABGELEHNT, true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, EINKOMMEN_EKV2_ANGENOMMEN, false);
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(BigDecimal.ZERO, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahrPlus1());
		Assert.assertNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());

		Assert.assertEquals(EINKOMMEN_EKV2_ANGENOMMEN, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahrPlus2());
		Assert.assertEquals(TestDataUtil.STICHTAG_EKV_2, gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
	}

	@Test
	public void testEinkommensverschlechterung2016Angenommen2017Angenommen() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, EINKOMMEN_EKV_ANGENOMMEN, true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, EINKOMMEN_EKV2_ANGENOMMEN, false);
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(EINKOMMEN_EKV_ANGENOMMEN, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahrPlus1());
		Assert.assertEquals(TestDataUtil.STICHTAG_EKV_1, gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());

		Assert.assertEquals(EINKOMMEN_EKV2_ANGENOMMEN, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahrPlus2());
		Assert.assertEquals(TestDataUtil.STICHTAG_EKV_2, gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
	}

	@Test
	public void testEinkommensverschlechterung2016Abgelehnt2017Abgelehnt() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.setFinanzielleSituation(gesuch, EINKOMMEN_FINANZIELLE_SITUATION);
		TestDataUtil.setEinkommensverschlechterung(gesuch, EINKOMMEN_EKV_ABGELEHNT, true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, EINKOMMEN_EKV2_ABGELEHNT, false);
		TestDataUtil.calculateFinanzDaten(gesuch);

		Assert.assertEquals(EINKOMMEN_FINANZIELLE_SITUATION, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahr());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), gesuch.getFinanzDatenDTO().getDatumVonBasisjahr());

		Assert.assertEquals(BigDecimal.ZERO, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahrPlus1());
		Assert.assertNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus1());

		Assert.assertEquals(BigDecimal.ZERO, gesuch.getFinanzDatenDTO().getMassgebendesEinkommenBasisjahrPlus2());
		Assert.assertNull(gesuch.getFinanzDatenDTO().getDatumVonBasisjahrPlus2());
	}


	@Test
	public void testCalculateFamiliengroesseNullGesuch() {
		double familiengroesse = finSitRechner.calculateFamiliengroesse(null, null);
		Assert.assertEquals(0, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseNullDate() {
		Gesuch gesuch = new Gesuch();
		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, null);
		Assert.assertEquals(0, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseNoGesuchSteller() {
		Gesuch gesuch = new Gesuch();
		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, DATE_2005);
		Assert.assertEquals(0, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseOneGesuchSteller() {
		Gesuch gesuch = new Gesuch();
		Gesuchsteller gesuchsteller = new Gesuchsteller();
		gesuch.setGesuchsteller1(gesuchsteller);
		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, DATE_2005);
		Assert.assertEquals(1, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseTwoGesuchSteller() {
		Gesuch gesuch = createGesuchWithTwoGesuchsteller();
		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, DATE_2005);
		Assert.assertEquals(2, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithGanzerAbzugKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.GANZER_ABZUG);

		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, LocalDate.now());
		Assert.assertEquals(3, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithHalberAbzugKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.HALBER_ABZUG);

		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, LocalDate.now());
		Assert.assertEquals(2.5, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithKeinAbzugKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.KEIN_ABZUG);

		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, LocalDate.now());
		Assert.assertEquals(2, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithKeineStErklaerungKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.KEINE_STEUERERKLAERUNG);

		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, LocalDate.now());
		Assert.assertEquals(3, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithWrongGeburtsdatum() {
		//das Kind war noch nicht geboren
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.KEINE_STEUERERKLAERUNG);

		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, LocalDate.of(2005, 5, 25));
		Assert.assertEquals(2, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithCorrectGeburtsdatum() {
		//das Kind war schon geboren
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.KEINE_STEUERERKLAERUNG);

		double familiengroesse = finSitRechner.calculateFamiliengroesse(gesuch, LocalDate.now());
		Assert.assertEquals(3, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateAbzugAufgrundFamiliengroesseZero() {
		Assert.assertEquals(0, finSitRechner.calculateAbzugAufgrundFamiliengroesse(LocalDate.now(), 0).intValue());
		Assert.assertEquals(0, finSitRechner.calculateAbzugAufgrundFamiliengroesse(LocalDate.now(), 1).intValue());
		Assert.assertEquals(0, finSitRechner.calculateAbzugAufgrundFamiliengroesse(LocalDate.now(), 2.5).intValue());
	}

	@Test
	public void testCalculateAbzugAufgrundFamiliengroesseThreeOrMore() {
		createEbeguParameter(EbeguParameterKey.PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_3);
		Assert.assertEquals(1100 * 3, finSitRechner.calculateAbzugAufgrundFamiliengroesse(DATE_2005, 3).intValue());
		Assert.assertEquals(1100 * 3.5, finSitRechner.calculateAbzugAufgrundFamiliengroesse(DATE_2005, 3.5).intValue(), 0);
	}

	@Test
	public void testCalculateAbzugAufgrundFamiliengroesseFourOrMore() {
		createEbeguParameter(EbeguParameterKey.PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_4);
		Assert.assertEquals(1100 * 4, finSitRechner.calculateAbzugAufgrundFamiliengroesse(DATE_2005, 4).intValue());
		Assert.assertEquals(1100 * 4.5, finSitRechner.calculateAbzugAufgrundFamiliengroesse(DATE_2005, 4.5).intValue(), 0);
	}

	@Test
	public void testCalculateAbzugAufgrundFamiliengroesseFiveOrMore() {
		createEbeguParameter(EbeguParameterKey.PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_5);
		Assert.assertEquals(1100 * 5, finSitRechner.calculateAbzugAufgrundFamiliengroesse(DATE_2005, 5).intValue());
		Assert.assertEquals(1100 * 5.5, finSitRechner.calculateAbzugAufgrundFamiliengroesse(DATE_2005, 5.5).intValue(), 0);
	}

	@Test
	public void testCalculateAbzugAufgrundFamiliengroesseSixOrMore() {
		createEbeguParameter(EbeguParameterKey.PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_6);
		Assert.assertEquals(1100 * 6, finSitRechner.calculateAbzugAufgrundFamiliengroesse(DATE_2005, 6).intValue());
		Assert.assertEquals(1100 * 99.5, finSitRechner.calculateAbzugAufgrundFamiliengroesse(DATE_2005, 99.5).intValue(), 0);
	}


	// HELP METHODS

	private void createEbeguParameter(EbeguParameterKey paramPauschalabzugProPersonFamiliengroesse4) {
		EbeguParameter ebeguParameter = new EbeguParameter();
		ebeguParameter.setName(paramPauschalabzugProPersonFamiliengroesse4);
		ebeguParameter.setValue("1100");
		ebeguParameter.setGueltigkeit(new DateRange(DATE_2005.getYear()));
		ebeguParameterService.saveEbeguParameter(ebeguParameter);
	}

	@Nonnull
	private Gesuch createGesuchWithTwoGesuchsteller() {
		Gesuch gesuch = new Gesuch();
		Gesuchsteller gesuchsteller = new Gesuchsteller();
		gesuch.setGesuchsteller1(gesuchsteller);
		gesuch.setGesuchsteller2(gesuchsteller);
		return gesuch;
	}

	@Nonnull
	private Gesuch createGesuchWithKind(Kinderabzug abzug) {
		Gesuch gesuch = createGesuchWithTwoGesuchsteller();
		Set<KindContainer> kindContainers = new LinkedHashSet<>();
		KindContainer kindContainer = new KindContainer();
		Kind kindJA = new Kind();
		kindJA.setKinderabzug(abzug);
		kindJA.setGeburtsdatum(LocalDate.of(2006, 5, 25));
		kindContainer.setKindJA(kindJA);
		kindContainers.add(kindContainer);
		gesuch.setKindContainers(kindContainers);
		return gesuch;
	}

}
