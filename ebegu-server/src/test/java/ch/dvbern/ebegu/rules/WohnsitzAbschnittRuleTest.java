package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tests fuer WohnsitzAbschnittRule
 */
public class WohnsitzAbschnittRuleTest {

	WohnsitzAbschnittRule wohnsitzRule = new WohnsitzAbschnittRule(Constants.DEFAULT_GUELTIGKEIT);


	@Test
	public void testCreateZeitAbschnitte() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();

		List<GesuchstellerAdresseContainer> adressen1 = new ArrayList<>();
		final GesuchstellerAdresseContainer adresse1GS1 = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuch.getGesuchsteller1());
		adresse1GS1.getGesuchstellerAdresseJA().setNichtInGemeinde(false);
		adresse1GS1.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(Constants.START_OF_TIME, LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.JANUARY, 25)));
		adressen1.add(adresse1GS1);
		final GesuchstellerAdresseContainer adresse2GS1 = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuch.getGesuchsteller1());
		adresse2GS1.getGesuchstellerAdresseJA().setNichtInGemeinde(true);
		adresse2GS1.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.JANUARY, 26), Constants.END_OF_TIME));
		adressen1.add(adresse2GS1);
		gesuch.getGesuchsteller1().setAdressen(adressen1);

		List<GesuchstellerAdresseContainer> adressen2 = new ArrayList<>();
		final GesuchstellerAdresseContainer adresse1GS2 = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuch.getGesuchsteller2());
		adresse1GS2.getGesuchstellerAdresseJA().setNichtInGemeinde(true);
		adresse1GS2.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(Constants.START_OF_TIME, LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.APRIL, 25)));
		adressen2.add(adresse1GS2);
		final GesuchstellerAdresseContainer adresse2GS2 = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuch.getGesuchsteller2());
		adresse2GS2.getGesuchstellerAdresseJA().setNichtInGemeinde(false);
		adresse2GS2.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.APRIL, 26), Constants.END_OF_TIME));
		adressen2.add(adresse2GS2);
		gesuch.getGesuchsteller2().setAdressen(adressen2);

		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitte = wohnsitzRule.createVerfuegungsZeitabschnitte(betreuung, Collections.emptyList());

		Assert.assertNotNull(verfuegungsZeitabschnitte);
		// Es werden 4 Abschnitte erwartet weil sie danach noch gemerged werden muessen
		Assert.assertEquals(4, verfuegungsZeitabschnitte.size());

		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), verfuegungsZeitabschnitte.get(0).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.MARCH, 31), verfuegungsZeitabschnitte.get(0).getGueltigkeit().getGueltigBis());
		Assert.assertFalse(verfuegungsZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.APRIL, 1), verfuegungsZeitabschnitte.get(1).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis(), verfuegungsZeitabschnitte.get(1).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), verfuegungsZeitabschnitte.get(2).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.APRIL, 25), verfuegungsZeitabschnitte.get(2).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.APRIL, 26), verfuegungsZeitabschnitte.get(3).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis(), verfuegungsZeitabschnitte.get(3).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(3).isWohnsitzNichtInGemeindeGS1());
		Assert.assertFalse(verfuegungsZeitabschnitte.get(3).isWohnsitzNichtInGemeindeGS2());


		final List<VerfuegungZeitabschnitt> mergedZerfuegungZeitabschnitte = wohnsitzRule.mergeZeitabschnitte(verfuegungsZeitabschnitte);

		Assert.assertEquals(3, mergedZerfuegungZeitabschnitte.size());

		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), mergedZerfuegungZeitabschnitte.get(0).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.MARCH, 31), mergedZerfuegungZeitabschnitte.get(0).getGueltigkeit().getGueltigBis());
		Assert.assertFalse(mergedZerfuegungZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.APRIL, 1), mergedZerfuegungZeitabschnitte.get(1).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.APRIL, 25), mergedZerfuegungZeitabschnitte.get(1).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.APRIL, 26), mergedZerfuegungZeitabschnitte.get(2).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis(), mergedZerfuegungZeitabschnitte.get(2).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS1());
		Assert.assertFalse(mergedZerfuegungZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS2());
	}


	@Test
	public void testCreateZeitAbschnitteFamSituationMutationFrom1GSTo2GS() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();

		createAdressenForGS1(gesuch);

		createAdressenForGS2(gesuch);

		gesuch.getFamiliensituationContainer().setFamiliensituationErstgesuch(new Familiensituation());
		gesuch.extractFamiliensituationErstgesuch().setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		gesuch.extractFamiliensituationErstgesuch().setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		gesuch.extractFamiliensituation().setAenderungPer(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.MARCH, 26));

		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitte = wohnsitzRule.createVerfuegungsZeitabschnitte(betreuung, Collections.emptyList());

		Assert.assertNotNull(verfuegungsZeitabschnitte);
		Assert.assertEquals(3, verfuegungsZeitabschnitte.size());

		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), verfuegungsZeitabschnitte.get(0).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.JANUARY, 31), verfuegungsZeitabschnitte.get(0).getGueltigkeit().getGueltigBis());
		Assert.assertFalse(verfuegungsZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.FEBRUARY, 1), verfuegungsZeitabschnitte.get(1).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis(), verfuegungsZeitabschnitte.get(1).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.MARCH, 26), verfuegungsZeitabschnitte.get(2).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis(), verfuegungsZeitabschnitte.get(2).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS1());
		Assert.assertFalse(verfuegungsZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS2());


		final List<VerfuegungZeitabschnitt> mergedZerfuegungZeitabschnitte = wohnsitzRule.mergeZeitabschnitte(verfuegungsZeitabschnitte);

		Assert.assertEquals(3, mergedZerfuegungZeitabschnitte.size());

		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), mergedZerfuegungZeitabschnitte.get(0).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.JANUARY, 31), mergedZerfuegungZeitabschnitte.get(0).getGueltigkeit().getGueltigBis());
		Assert.assertFalse(mergedZerfuegungZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.FEBRUARY, 1), mergedZerfuegungZeitabschnitte.get(1).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.MARCH, 25), mergedZerfuegungZeitabschnitte.get(1).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.MARCH, 26), mergedZerfuegungZeitabschnitte.get(2).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis(), mergedZerfuegungZeitabschnitte.get(2).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS1());
		Assert.assertFalse(mergedZerfuegungZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS2());
	}


	@Test
	public void testCreateZeitAbschnitteFamSituationMutationFrom2GSTo1GS() {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();

		createAdressenForGS1(gesuch);

		createAdressenForGS2(gesuch);

		gesuch.getFamiliensituationContainer().setFamiliensituationErstgesuch(new Familiensituation());
		gesuch.extractFamiliensituationErstgesuch().setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		gesuch.extractFamiliensituation().setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		gesuch.extractFamiliensituation().setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		gesuch.extractFamiliensituation().setAenderungPer(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.MARCH, 26));

		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitte = wohnsitzRule.createVerfuegungsZeitabschnitte(betreuung, Collections.emptyList());

		Assert.assertNotNull(verfuegungsZeitabschnitte);
		Assert.assertEquals(4, verfuegungsZeitabschnitte.size());

		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), verfuegungsZeitabschnitte.get(0).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.JANUARY, 31), verfuegungsZeitabschnitte.get(0).getGueltigkeit().getGueltigBis());
		Assert.assertFalse(verfuegungsZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.FEBRUARY, 1), verfuegungsZeitabschnitte.get(1).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis(), verfuegungsZeitabschnitte.get(1).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), verfuegungsZeitabschnitte.get(2).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.FEBRUARY, 25), verfuegungsZeitabschnitte.get(2).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.FEBRUARY, 26), verfuegungsZeitabschnitte.get(3).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.MARCH, 26), verfuegungsZeitabschnitte.get(3).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(3).isWohnsitzNichtInGemeindeGS1());
		Assert.assertFalse(verfuegungsZeitabschnitte.get(3).isWohnsitzNichtInGemeindeGS2());


		final List<VerfuegungZeitabschnitt> mergedZerfuegungZeitabschnitte = wohnsitzRule.mergeZeitabschnitte(verfuegungsZeitabschnitte);

		Assert.assertEquals(4, mergedZerfuegungZeitabschnitte.size());

		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), mergedZerfuegungZeitabschnitte.get(0).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.JANUARY, 31), mergedZerfuegungZeitabschnitte.get(0).getGueltigkeit().getGueltigBis());
		Assert.assertFalse(mergedZerfuegungZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.FEBRUARY, 1), mergedZerfuegungZeitabschnitte.get(1).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.FEBRUARY, 25), mergedZerfuegungZeitabschnitte.get(1).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.FEBRUARY, 26), mergedZerfuegungZeitabschnitte.get(2).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.MARCH, 26), mergedZerfuegungZeitabschnitte.get(2).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS1());
		Assert.assertFalse(mergedZerfuegungZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.MARCH, 27), mergedZerfuegungZeitabschnitte.get(3).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis(), mergedZerfuegungZeitabschnitte.get(3).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(3).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(mergedZerfuegungZeitabschnitte.get(3).isWohnsitzNichtInGemeindeGS2());
	}

	@Test
	public void testNichtInBernInBernNichtInBern() {
		// der GS wohnt zuerst nicht in Bern, danach zieht er ein und dann wieder weg. Das Wegziehen sollte erst 2 Monaten danach beruecksichtigt werden
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(true);
		Gesuch gesuch = betreuung.extractGesuch();

		List<GesuchstellerAdresseContainer> adressen1 = new ArrayList<>();
		final GesuchstellerAdresseContainer adresse1GS1 = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuch.getGesuchsteller1());
		adresse1GS1.getGesuchstellerAdresseJA().setNichtInGemeinde(true);
		adresse1GS1.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(Constants.START_OF_TIME, LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.JANUARY, 25)));
		adressen1.add(adresse1GS1);

		final GesuchstellerAdresseContainer adresse2GS1 = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuch.getGesuchsteller1());
		adresse2GS1.getGesuchstellerAdresseJA().setNichtInGemeinde(false);
		adresse2GS1.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.JANUARY, 26), LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.FEBRUARY, 26)));
		adressen1.add(adresse2GS1);

		final GesuchstellerAdresseContainer adresse3GS1 = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuch.getGesuchsteller1());
		adresse3GS1.getGesuchstellerAdresseJA().setNichtInGemeinde(true);
		adresse3GS1.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.FEBRUARY, 27), Constants.END_OF_TIME));
		adressen1.add(adresse3GS1);

		gesuch.getGesuchsteller1().setAdressen(adressen1);

		final List<VerfuegungZeitabschnitt> verfuegungsZeitabschnitte = wohnsitzRule.createVerfuegungsZeitabschnitte(betreuung, Collections.emptyList());

		Assert.assertNotNull(verfuegungsZeitabschnitte);
		Assert.assertEquals(3, verfuegungsZeitabschnitte.size());

		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), verfuegungsZeitabschnitte.get(0).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.JANUARY, 25), verfuegungsZeitabschnitte.get(0).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(0).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.JANUARY, 26), verfuegungsZeitabschnitte.get(1).getGueltigkeit().getGueltigAb());
		// muss 2 Monate spaeter enden
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.APRIL, 30), verfuegungsZeitabschnitte.get(1).getGueltigkeit().getGueltigBis());
		Assert.assertFalse(verfuegungsZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(1).isWohnsitzNichtInGemeindeGS2());

		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.MAY, 1), verfuegungsZeitabschnitte.get(2).getGueltigkeit().getGueltigAb());
		Assert.assertEquals(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis(), verfuegungsZeitabschnitte.get(2).getGueltigkeit().getGueltigBis());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(verfuegungsZeitabschnitte.get(2).isWohnsitzNichtInGemeindeGS2());
	}


	// HELP METHODS

	private void createAdressenForGS1(Gesuch gesuch) {
		List<GesuchstellerAdresseContainer> adressen1 = new ArrayList<>();
		final GesuchstellerAdresseContainer adresse1GS1 = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuch.getGesuchsteller1());
		adresse1GS1.getGesuchstellerAdresseJA().setNichtInGemeinde(false);
		adresse1GS1.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(Constants.START_OF_TIME, LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.NOVEMBER, 25)));
		adressen1.add(adresse1GS1);
		final GesuchstellerAdresseContainer adresse2GS1 = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuch.getGesuchsteller1());
		adresse2GS1.getGesuchstellerAdresseJA().setNichtInGemeinde(true);
		adresse2GS1.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.NOVEMBER, 26), Constants.END_OF_TIME));
		adressen1.add(adresse2GS1);
		gesuch.getGesuchsteller1().setAdressen(adressen1);
	}

	private void createAdressenForGS2(Gesuch gesuch) {
		List<GesuchstellerAdresseContainer> adressen2 = new ArrayList<>();
		final GesuchstellerAdresseContainer adresse1GS2 = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuch.getGesuchsteller2());
		adresse1GS2.getGesuchstellerAdresseJA().setNichtInGemeinde(true);
		adresse1GS2.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(Constants.START_OF_TIME, LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.FEBRUARY, 25)));
		adressen2.add(adresse1GS2);
		final GesuchstellerAdresseContainer adresse2GS2 = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuch.getGesuchsteller2());
		adresse2GS2.getGesuchstellerAdresseJA().setNichtInGemeinde(false);
		adresse2GS2.getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.FEBRUARY, 26), Constants.END_OF_TIME));
		adressen2.add(adresse2GS2);
		gesuch.getGesuchsteller2().setAdressen(adressen2);
	}
}
