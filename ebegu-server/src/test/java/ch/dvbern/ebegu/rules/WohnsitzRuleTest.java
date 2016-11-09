package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Tests für WohnsitzRule
 */
@SuppressWarnings("ConstantConditions")
public class WohnsitzRuleTest {

	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);


	@Test
	public void testNormalfallBeideAdresseInBern() {
		Betreuung betreuung = createTestdata(true);
		betreuung.extractGesuch().getGesuchsteller1().addAdresse(createGesuchstellerAdresse(START_PERIODE, ENDE_PERIODE, false));
		List<VerfuegungZeitabschnitt> zeitabschnittList = EbeguRuleTestsHelper.calculate(betreuung);
		Assert.assertNotNull(zeitabschnittList);
		Assert.assertEquals(1, zeitabschnittList.size());
		VerfuegungZeitabschnitt abschnittInBern = zeitabschnittList.get(0);
		Assert.assertFalse(abschnittInBern.isWohnsitzNichtInGemeindeGS1());
		Assert.assertEquals(100, abschnittInBern.getAnspruchberechtigtesPensum());
		Assert.assertEquals(100, abschnittInBern.getBgPensum());
	}

	@Test
	public void testEinGesuchstellerAdresseNichtBern() {
		Betreuung betreuung = createTestdata(false);
		betreuung.extractGesuch().getGesuchsteller1().addAdresse(createGesuchstellerAdresse(START_PERIODE, ENDE_PERIODE, true));
		List<VerfuegungZeitabschnitt> zeitabschnittList = EbeguRuleTestsHelper.calculate(betreuung);
		Assert.assertNotNull(zeitabschnittList);
		Assert.assertEquals(1, zeitabschnittList.size());
		VerfuegungZeitabschnitt abschnitt = zeitabschnittList.get(0);
		Assert.assertTrue(abschnitt.isWohnsitzNichtInGemeindeGS1());
		Assert.assertEquals(0, abschnitt.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnitt.getBgPensum());
	}

	@Test
	public void testEinGesuchstellerAdresseInBern() {
		Betreuung betreuung = createTestdata(false);
		betreuung.extractGesuch().getGesuchsteller1().addAdresse(createGesuchstellerAdresse(START_PERIODE, ENDE_PERIODE, false));
		List<VerfuegungZeitabschnitt> zeitabschnittList = EbeguRuleTestsHelper.calculate(betreuung);
		Assert.assertNotNull(zeitabschnittList);
		Assert.assertEquals(1, zeitabschnittList.size());
		VerfuegungZeitabschnitt abschnittInBern = zeitabschnittList.get(0);
		Assert.assertFalse(abschnittInBern.isWohnsitzNichtInGemeindeGS1());
		Assert.assertEquals(100, abschnittInBern.getAnspruchberechtigtesPensum());
		Assert.assertEquals(100, abschnittInBern.getBgPensum());
	}

	@Test
	public void testZweiGesuchstellerEinerDavonInBern() {
		Betreuung betreuung = createTestdata(true);
		betreuung.extractGesuch().getGesuchsteller1().addAdresse(createGesuchstellerAdresse(START_PERIODE, ENDE_PERIODE, false));
		betreuung.extractGesuch().getGesuchsteller2().addAdresse(createGesuchstellerAdresse(START_PERIODE, ENDE_PERIODE, true));
		List<VerfuegungZeitabschnitt> zeitabschnittList = EbeguRuleTestsHelper.calculate(betreuung);
		Assert.assertNotNull(zeitabschnittList);
		Assert.assertEquals(1, zeitabschnittList.size());
		VerfuegungZeitabschnitt abschnittInBern = zeitabschnittList.get(0);
		Assert.assertFalse(abschnittInBern.isWohnsitzNichtInGemeindeGS1());
		Assert.assertEquals(100, abschnittInBern.getAnspruchberechtigtesPensum());
		Assert.assertEquals(100, abschnittInBern.getBgPensum());
	}

	@Test
	public void testZweiGesuchstellerNichtInBernWithUmzugGS2InBern() {
		Betreuung betreuung = createTestdata(true);
		betreuung.extractGesuch().getGesuchsteller1().addAdresse(createGesuchstellerAdresse(START_PERIODE, ENDE_PERIODE, true));

		betreuung.extractGesuch().getGesuchsteller2()
			.addAdresse(createGesuchstellerAdresse(START_PERIODE, LocalDate.of(2016, Month.DECEMBER, 15), true)); // nur wenn Gesuchsperiode 2016/2017
		betreuung.extractGesuch().getGesuchsteller2()
			.addAdresse(createGesuchstellerAdresse(LocalDate.of(2016, Month.DECEMBER, 16), ENDE_PERIODE, false)); // nur wenn Gesuchsperiode 2016/2017

		List<VerfuegungZeitabschnitt> zeitabschnittList = EbeguRuleTestsHelper.calculate(betreuung);
		Assert.assertNotNull(zeitabschnittList);
		Assert.assertEquals(2, zeitabschnittList.size());

		VerfuegungZeitabschnitt abschnittNichtInBern = zeitabschnittList.get(0);
		Assert.assertTrue(abschnittNichtInBern.isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(abschnittNichtInBern.isWohnsitzNichtInGemeindeGS2());
		Assert.assertEquals(0, abschnittNichtInBern.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnittNichtInBern.getBgPensum());
		Assert.assertEquals(START_PERIODE, abschnittNichtInBern.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(2016, Month.DECEMBER, 15), abschnittNichtInBern.getGueltigkeit().getGueltigBis());

		VerfuegungZeitabschnitt abschnittInBern = zeitabschnittList.get(1);
		Assert.assertTrue(abschnittInBern.isWohnsitzNichtInGemeindeGS1());
		Assert.assertFalse(abschnittInBern.isWohnsitzNichtInGemeindeGS2());
		Assert.assertEquals(100, abschnittInBern.getAnspruchberechtigtesPensum());
		Assert.assertEquals(100, abschnittInBern.getBgPensum());
		Assert.assertEquals(LocalDate.of(2016, Month.DECEMBER, 16), abschnittInBern.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(ENDE_PERIODE, abschnittInBern.getGueltigkeit().getGueltigBis());
	}

	@Test
	public void testZuzug() {
		LocalDate zuzugsDatum = LocalDate.of(2016, Month.OCTOBER, 16);
		Betreuung betreuung = createTestdata(true);
		betreuung.extractGesuch().getGesuchsteller1().addAdresse(createGesuchstellerAdresse(START_PERIODE, zuzugsDatum.minusDays(1), true));
		betreuung.extractGesuch().getGesuchsteller1().addAdresse(createGesuchstellerAdresse(zuzugsDatum, ENDE_PERIODE, false));
		betreuung.extractGesuch().getGesuchsteller2().addAdresse(createGesuchstellerAdresse(START_PERIODE, ENDE_PERIODE, true));
		List<VerfuegungZeitabschnitt> zeitabschnittList = EbeguRuleTestsHelper.calculate(betreuung);
		Assert.assertNotNull(zeitabschnittList);
		Assert.assertEquals(2, zeitabschnittList.size());
		VerfuegungZeitabschnitt abschnittNichtInBern = zeitabschnittList.get(0);
		Assert.assertTrue(abschnittNichtInBern.isWohnsitzNichtInGemeindeGS1());
		Assert.assertEquals(0, abschnittNichtInBern.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnittNichtInBern.getBgPensum());
		VerfuegungZeitabschnitt abschnittInBern = zeitabschnittList.get(1);
		Assert.assertEquals(zuzugsDatum, abschnittInBern.getGueltigkeit().getGueltigAb());
		Assert.assertFalse(abschnittInBern.isWohnsitzNichtInGemeindeGS1());
		Assert.assertEquals(100, abschnittInBern.getAnspruchberechtigtesPensum());
		Assert.assertEquals(100, abschnittInBern.getBgPensum());
	}

	@Test
	public void testWegzug() {
		LocalDate wegzugsDatum = LocalDate.of(2016, Month.OCTOBER, 16);
		Betreuung betreuung = createTestdata(true);
		betreuung.extractGesuch().getGesuchsteller1().addAdresse(createGesuchstellerAdresse(START_PERIODE, wegzugsDatum.minusDays(1), false));
		betreuung.extractGesuch().getGesuchsteller1().addAdresse(createGesuchstellerAdresse(wegzugsDatum, ENDE_PERIODE, true));
		betreuung.extractGesuch().getGesuchsteller2().addAdresse(createGesuchstellerAdresse(START_PERIODE, ENDE_PERIODE, true));
		List<VerfuegungZeitabschnitt> zeitabschnittList = EbeguRuleTestsHelper.calculate(betreuung);
		Assert.assertNotNull(zeitabschnittList);
		Assert.assertEquals(2, zeitabschnittList.size());
		VerfuegungZeitabschnitt abschnittInBern = zeitabschnittList.get(0);
		Assert.assertFalse(abschnittInBern.isWohnsitzNichtInGemeindeGS1());
		Assert.assertEquals(100, abschnittInBern.getAnspruchberechtigtesPensum());
		Assert.assertEquals(100, abschnittInBern.getBgPensum());
		VerfuegungZeitabschnitt abschnittNichtInBern = zeitabschnittList.get(1);
		Assert.assertTrue(abschnittNichtInBern.isWohnsitzNichtInGemeindeGS1());
		//Anspruch noch 2 Monate nach wegzug auf Ende Monat
		Assert.assertEquals(wegzugsDatum.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth()), abschnittInBern.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(0, abschnittNichtInBern.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnittNichtInBern.getBgPensum());
	}

	@Test
	public void testZweiGesuchstellerEinerDavonInBernMutationFamiliensituation() {
		Betreuung betreuung = createTestdata(true);
		final Gesuch gesuch = betreuung.extractGesuch();

		gesuch.setFamiliensituationErstgesuch(new Familiensituation());
		gesuch.getFamiliensituationErstgesuch().setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		gesuch.getFamiliensituationErstgesuch().setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		gesuch.getFamiliensituation().setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		gesuch.getFamiliensituation().setAenderungPer(LocalDate.of(2017, Month.MARCH, 26));

		gesuch.getGesuchsteller1().addAdresse(createGesuchstellerAdresse(START_PERIODE, ENDE_PERIODE, true));
		gesuch.getGesuchsteller2().addAdresse(createGesuchstellerAdresse(START_PERIODE, ENDE_PERIODE, false));
		List<VerfuegungZeitabschnitt> zeitabschnittList = EbeguRuleTestsHelper.calculate(betreuung);
		Assert.assertNotNull(zeitabschnittList);
		Assert.assertEquals(2, zeitabschnittList.size());

		VerfuegungZeitabschnitt abschnittInBern1 = zeitabschnittList.get(0);
		Assert.assertTrue(abschnittInBern1.isWohnsitzNichtInGemeindeGS1());
		Assert.assertEquals(0, abschnittInBern1.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnittInBern1.getBgPensum());

		VerfuegungZeitabschnitt abschnittInBern2 = zeitabschnittList.get(1);
		Assert.assertFalse(abschnittInBern2.isWohnsitzNichtInGemeindeGS2());
		Assert.assertEquals(100, abschnittInBern2.getAnspruchberechtigtesPensum());
		Assert.assertEquals(100, abschnittInBern2.getBgPensum());
	}



	private Betreuung createTestdata(boolean zweigesuchsteller) {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(zweigesuchsteller);
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.KITA);
		betreuung.setBetreuungspensumContainers(new LinkedHashSet<>());
		BetreuungspensumContainer betreuungspensumContainer = new BetreuungspensumContainer();
		betreuungspensumContainer.setBetreuung(betreuung);
		DateRange gueltigkeit = new DateRange(START_PERIODE, ENDE_PERIODE);
		betreuungspensumContainer.setBetreuungspensumJA(new Betreuungspensum(gueltigkeit));
		betreuungspensumContainer.getBetreuungspensumJA().setPensum(100);
		betreuung.getBetreuungspensumContainers().add(betreuungspensumContainer);
		betreuung.getKind().getKindJA().setWohnhaftImGleichenHaushalt(100);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 100, 0));
		if (zweigesuchsteller) {
			betreuung.getKind().getGesuch().getGesuchsteller2().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 100, 0));
		}
		return betreuung;
	}

	private GesuchstellerAdresse createGesuchstellerAdresse(LocalDate von, LocalDate bis, boolean nichtInGemeinde) {
		GesuchstellerAdresse adresse = TestDataUtil.createDefaultGesuchstellerAdresse();
		adresse.setNichtInGemeinde(nichtInGemeinde);
		adresse.getGueltigkeit().setGueltigAb(von);
		adresse.getGueltigkeit().setGueltigBis(bis);
		return adresse;
	}
}
