package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.Kinderabzug;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.Constants;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;


/**
 * Tests fuer FamilienabzugAbschnittRule
 */
public class FamilienabzugAbschnittRuleTest {


	private final BigDecimal pauschalabzugProPersonFamiliengroesse3 = BigDecimal.valueOf(1000);
	private final BigDecimal pauschalabzugProPersonFamiliengroesse4 = BigDecimal.valueOf(2000);
	private final BigDecimal pauschalabzugProPersonFamiliengroesse5 = BigDecimal.valueOf(3000);
	private final BigDecimal pauschalabzugProPersonFamiliengroesse6 = BigDecimal.valueOf(4000);

	private static final double DELTA = 1e-15;
	public static final LocalDate DATE_2005 = LocalDate.of(2005, 12, 31);

	private final FamilienabzugAbschnittRule famabAbschnittRule =
		new FamilienabzugAbschnittRule(Constants.DEFAULT_GUELTIGKEIT, pauschalabzugProPersonFamiliengroesse3,
			pauschalabzugProPersonFamiliengroesse4, pauschalabzugProPersonFamiliengroesse5,pauschalabzugProPersonFamiliengroesse6);


	@Test
	public void test2PKeinAbzug() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		gesuch.setKindContainers(new HashSet<KindContainer>());
		final KindContainer defaultKindContainer = TestDataUtil.createDefaultKindContainer();
		gesuch.getKindContainers().add(defaultKindContainer);

		List<VerfuegungZeitabschnitt> zeitabschnitte = famabAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(1, zeitabschnitte.size());
		final VerfuegungZeitabschnitt verfuegungZeitabschnitt = zeitabschnitte.iterator().next();
		Assert.assertEquals(0, verfuegungZeitabschnitt.getAbzugFamGroesse().compareTo(BigDecimal.ZERO));
	}

	@Test
	public void test3P_Abzug() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(false);
		Gesuch gesuch = betreuung.extractGesuch();
		gesuch.setKindContainers(new HashSet<KindContainer>());
		final KindContainer defaultKindContainer1 = TestDataUtil.createDefaultKindContainer();
		final KindContainer defaultKindContainer2 = TestDataUtil.createDefaultKindContainer();

		gesuch.getKindContainers().add(defaultKindContainer1);
		gesuch.getKindContainers().add(defaultKindContainer2);

		List<VerfuegungZeitabschnitt> zeitabschnitte = famabAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(1, zeitabschnitte.size());
		final VerfuegungZeitabschnitt verfuegungZeitabschnitt = zeitabschnitte.iterator().next();
		Assert.assertEquals(0, verfuegungZeitabschnitt.getAbzugFamGroesse().compareTo(pauschalabzugProPersonFamiliengroesse3.multiply(BigDecimal.valueOf(3))));
	}

	@Test
	public void test4P_Abzug() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();
		gesuch.setKindContainers(new HashSet<KindContainer>());
		final KindContainer defaultKindContainer1 = TestDataUtil.createDefaultKindContainer();
		final KindContainer defaultKindContainer2 = TestDataUtil.createDefaultKindContainer();

		gesuch.getKindContainers().add(defaultKindContainer1);
		gesuch.getKindContainers().add(defaultKindContainer2);

		List<VerfuegungZeitabschnitt> zeitabschnitte = famabAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(1, zeitabschnitte.size());
		final VerfuegungZeitabschnitt verfuegungZeitabschnitt = zeitabschnitte.iterator().next();
		Assert.assertEquals(0, verfuegungZeitabschnitt.getAbzugFamGroesse().compareTo(pauschalabzugProPersonFamiliengroesse4.multiply(BigDecimal.valueOf(4))));
	}

	@Test
	public void test3P_Abzug_Kind_waehrendPeriode() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();
		gesuch.setKindContainers(new HashSet<KindContainer>());
		final KindContainer defaultKindContainer1 = TestDataUtil.createDefaultKindContainer();
		final KindContainer defaultKindContainer2 = TestDataUtil.createDefaultKindContainer();
		final LocalDate geburtsdatum = LocalDate.of(2017, 1, 10);
		defaultKindContainer2.getKindJA().setGeburtsdatum(geburtsdatum);

		gesuch.getKindContainers().add(defaultKindContainer1);
		gesuch.getKindContainers().add(defaultKindContainer2);

		List<VerfuegungZeitabschnitt> zeitabschnitte = famabAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(2, zeitabschnitte.size());

		final Iterator<VerfuegungZeitabschnitt> iterator = zeitabschnitte.iterator();
		final VerfuegungZeitabschnitt verfuegungZeitabschnitt1 = iterator.next();
		Assert.assertEquals(0, verfuegungZeitabschnitt1.getAbzugFamGroesse().compareTo(pauschalabzugProPersonFamiliengroesse3.multiply(BigDecimal.valueOf(3))));
		final LocalDate withDayOfMonth = geburtsdatum.plusMonths(1).withDayOfMonth(1);
		Assert.assertEquals(0, verfuegungZeitabschnitt1.getGueltigkeit().getGueltigBis().compareTo(
			withDayOfMonth.minusDays(1)));

		final VerfuegungZeitabschnitt verfuegungZeitabschnitt2 = iterator.next();
		Assert.assertEquals(0, verfuegungZeitabschnitt2.getAbzugFamGroesse().compareTo(pauschalabzugProPersonFamiliengroesse4.multiply(BigDecimal.valueOf(4))));
		Assert.assertEquals(0, verfuegungZeitabschnitt2.getGueltigkeit().getGueltigAb().compareTo(withDayOfMonth));
	}

	@Test
	public void test3P_Abzug_Zwiling_waehrendPeriode() throws Exception {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();
		gesuch.setKindContainers(new HashSet<KindContainer>());
		final KindContainer defaultKindContainer1 = TestDataUtil.createDefaultKindContainer();
		final KindContainer defaultKindContainer2 = TestDataUtil.createDefaultKindContainer();
		final LocalDate geburtsdatum = LocalDate.of(2017, 1, 10);
		defaultKindContainer2.getKindJA().setGeburtsdatum(geburtsdatum);

		final KindContainer defaultKindContainer3 = TestDataUtil.createDefaultKindContainer();
		defaultKindContainer3.getKindJA().setGeburtsdatum(geburtsdatum);

		gesuch.getKindContainers().add(defaultKindContainer1);
		gesuch.getKindContainers().add(defaultKindContainer2);
		gesuch.getKindContainers().add(defaultKindContainer3);

		List<VerfuegungZeitabschnitt> zeitabschnitte = famabAbschnittRule.createVerfuegungsZeitabschnitte(betreuung, new ArrayList<>());
		Assert.assertNotNull(zeitabschnitte);
		Assert.assertEquals(2, zeitabschnitte.size());

		final Iterator<VerfuegungZeitabschnitt> iterator = zeitabschnitte.iterator();
		final VerfuegungZeitabschnitt verfuegungZeitabschnitt1 = iterator.next();
		Assert.assertEquals(0, verfuegungZeitabschnitt1.getAbzugFamGroesse().compareTo(pauschalabzugProPersonFamiliengroesse3.multiply(BigDecimal.valueOf(3))));
		final LocalDate withDayOfMonth = geburtsdatum.plusMonths(1).withDayOfMonth(1);
		Assert.assertEquals(0, verfuegungZeitabschnitt1.getGueltigkeit().getGueltigBis().compareTo(
			withDayOfMonth.minusDays(1)));

		final VerfuegungZeitabschnitt verfuegungZeitabschnitt2 = iterator.next();
		Assert.assertEquals(0, verfuegungZeitabschnitt2.getAbzugFamGroesse().compareTo(pauschalabzugProPersonFamiliengroesse5.multiply(BigDecimal.valueOf(5))));
		Assert.assertEquals(0, verfuegungZeitabschnitt2.getGueltigkeit().getGueltigAb().compareTo(withDayOfMonth));
	}

	@Test
	public void testCalculateFamiliengroesseNullGesuch() {
		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(null, null);
		Assert.assertEquals(0, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseNullDate() {
		Gesuch gesuch = new Gesuch();
		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, null);
		Assert.assertEquals(0, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseNoGesuchSteller() {
		Gesuch gesuch = new Gesuch();
		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, DATE_2005);
		Assert.assertEquals(0, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseOneGesuchSteller() {
		Gesuch gesuch = new Gesuch();
		Gesuchsteller gesuchsteller = new Gesuchsteller();
		gesuch.setGesuchsteller1(gesuchsteller);
		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, DATE_2005);
		Assert.assertEquals(1, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseTwoGesuchSteller() {
		Gesuch gesuch = createGesuchWithTwoGesuchsteller();
		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, DATE_2005);
		Assert.assertEquals(2, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithGanzerAbzugKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.GANZER_ABZUG);

		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.now());
		Assert.assertEquals(3, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithHalberAbzugKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.HALBER_ABZUG);

		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.now());
		Assert.assertEquals(2.5, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithKeinAbzugKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.KEIN_ABZUG);

		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.now());
		Assert.assertEquals(2, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithKeineStErklaerungKind() {
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.KEINE_STEUERERKLAERUNG);

		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.now());
		Assert.assertEquals(3, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithWrongGeburtsdatum() {
		//das Kind war noch nicht geboren
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.KEINE_STEUERERKLAERUNG);

		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.of(2005, 5, 25));
		Assert.assertEquals(2, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateFamiliengroesseWithCorrectGeburtsdatum() {
		//das Kind war schon geboren
		Gesuch gesuch = createGesuchWithKind(Kinderabzug.KEINE_STEUERERKLAERUNG);

		double familiengroesse = famabAbschnittRule.calculateFamiliengroesse(gesuch, LocalDate.now());
		Assert.assertEquals(3, familiengroesse, DELTA);
	}

	@Test
	public void testCalculateAbzugAufgrundFamiliengroesseZero() {
		Assert.assertEquals(0, famabAbschnittRule.calculateAbzugAufgrundFamiliengroesse(0).intValue());
		Assert.assertEquals(0, famabAbschnittRule.calculateAbzugAufgrundFamiliengroesse(1).intValue());
		Assert.assertEquals(0, famabAbschnittRule.calculateAbzugAufgrundFamiliengroesse(2.5).intValue());
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
