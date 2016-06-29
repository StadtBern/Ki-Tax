package ch.dvbern.ebegu.tests.util;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.enums.Kinderabzug;
import ch.dvbern.ebegu.services.EbeguParameterService;
import ch.dvbern.ebegu.tests.AbstractEbeguTest;
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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests fuer FinanzielleSituationRechner
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class FinanzielleSituationRechnerTest extends AbstractEbeguTest {

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
		Set<KindContainer> kindContainers = new HashSet<>();
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
