/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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

	@Test
	public void testNormalfallBeideAdresseInBern() {
		Betreuung betreuung = createTestdata(true);
		final Gesuch gesuch = betreuung.extractGesuch();
		gesuch.getGesuchsteller1().addAdresse(createGesuchstellerAdresse(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, false, gesuch.getGesuchsteller1()));
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
		final Gesuch gesuch = betreuung.extractGesuch();
		gesuch.getGesuchsteller1().addAdresse(createGesuchstellerAdresse(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, true, gesuch.getGesuchsteller1()));
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
		final Gesuch gesuch = betreuung.extractGesuch();
		gesuch.getGesuchsteller1().addAdresse(createGesuchstellerAdresse(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, false, gesuch.getGesuchsteller1()));
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
		final Gesuch gesuch = betreuung.extractGesuch();
		gesuch.getGesuchsteller1().addAdresse(createGesuchstellerAdresse(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, false, gesuch.getGesuchsteller1()));
		gesuch.getGesuchsteller2().addAdresse(createGesuchstellerAdresse(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, true, gesuch.getGesuchsteller2()));
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
		final Gesuch gesuch = betreuung.extractGesuch();
		gesuch.getGesuchsteller1().addAdresse(createGesuchstellerAdresse(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, true, gesuch.getGesuchsteller1()));

		gesuch.getGesuchsteller2()
			.addAdresse(createGesuchstellerAdresse(TestDataUtil.START_PERIODE, LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.DECEMBER, 15), true, gesuch.getGesuchsteller2())); // nur wenn Gesuchsperiode TestDataUtil.PERIODE_JAHR_1/TestDataUtil.PERIODE_JAHR_2
		gesuch.getGesuchsteller2()
			.addAdresse(createGesuchstellerAdresse(LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.DECEMBER, 16), TestDataUtil.ENDE_PERIODE, false, gesuch.getGesuchsteller2())); // nur wenn Gesuchsperiode TestDataUtil.PERIODE_JAHR_1/TestDataUtil.PERIODE_JAHR_2

		List<VerfuegungZeitabschnitt> zeitabschnittList = EbeguRuleTestsHelper.calculate(betreuung);
		Assert.assertNotNull(zeitabschnittList);
		Assert.assertEquals(2, zeitabschnittList.size());

		VerfuegungZeitabschnitt abschnittNichtInBern = zeitabschnittList.get(0);
		Assert.assertTrue(abschnittNichtInBern.isWohnsitzNichtInGemeindeGS1());
		Assert.assertTrue(abschnittNichtInBern.isWohnsitzNichtInGemeindeGS2());
		Assert.assertEquals(0, abschnittNichtInBern.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnittNichtInBern.getBgPensum());
		Assert.assertEquals(TestDataUtil.START_PERIODE, abschnittNichtInBern.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.DECEMBER, 15), abschnittNichtInBern.getGueltigkeit().getGueltigBis());

		VerfuegungZeitabschnitt abschnittInBern = zeitabschnittList.get(1);
		Assert.assertTrue(abschnittInBern.isWohnsitzNichtInGemeindeGS1());
		Assert.assertFalse(abschnittInBern.isWohnsitzNichtInGemeindeGS2());
		Assert.assertEquals(100, abschnittInBern.getAnspruchberechtigtesPensum());
		Assert.assertEquals(100, abschnittInBern.getBgPensum());
		Assert.assertEquals(LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.DECEMBER, 16), abschnittInBern.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(TestDataUtil.ENDE_PERIODE, abschnittInBern.getGueltigkeit().getGueltigBis());
	}

	@Test
	public void testZuzug() {
		LocalDate zuzugsDatum = LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.OCTOBER, 16);
		Betreuung betreuung = createTestdata(true);
		final Gesuch gesuch = betreuung.extractGesuch();
		gesuch.getGesuchsteller1().addAdresse(createGesuchstellerAdresse(TestDataUtil.START_PERIODE, zuzugsDatum.minusDays(1), true, gesuch.getGesuchsteller1()));
		gesuch.getGesuchsteller1().addAdresse(createGesuchstellerAdresse(zuzugsDatum, TestDataUtil.ENDE_PERIODE, false, gesuch.getGesuchsteller1()));
		gesuch.getGesuchsteller2().addAdresse(createGesuchstellerAdresse(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, true, gesuch.getGesuchsteller2()));
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
		LocalDate wegzugsDatum = LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.OCTOBER, 16);
		Betreuung betreuung = createTestdata(true);
		final Gesuch gesuch = betreuung.extractGesuch();
		gesuch.getGesuchsteller1().addAdresse(createGesuchstellerAdresse(TestDataUtil.START_PERIODE, wegzugsDatum.minusDays(1), false, gesuch.getGesuchsteller1()));
		gesuch.getGesuchsteller1().addAdresse(createGesuchstellerAdresse(wegzugsDatum, TestDataUtil.ENDE_PERIODE, true, gesuch.getGesuchsteller1()));
		gesuch.getGesuchsteller2().addAdresse(createGesuchstellerAdresse(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, true, gesuch.getGesuchsteller2()));
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

		gesuch.getFamiliensituationContainer().setFamiliensituationErstgesuch(new Familiensituation());
		gesuch.extractFamiliensituationErstgesuch().setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		gesuch.extractFamiliensituationErstgesuch().setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		gesuch.extractFamiliensituation().setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		gesuch.extractFamiliensituation().setAenderungPer(LocalDate.of(TestDataUtil.PERIODE_JAHR_2, Month.MARCH, 26));

		gesuch.getGesuchsteller1().addAdresse(createGesuchstellerAdresse(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, true, gesuch.getGesuchsteller1()));
		gesuch.getGesuchsteller2().addAdresse(createGesuchstellerAdresse(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, false, gesuch.getGesuchsteller2()));
		List<VerfuegungZeitabschnitt> zeitabschnittList = EbeguRuleTestsHelper.calculate(betreuung);
		Assert.assertNotNull(zeitabschnittList);
		Assert.assertEquals(3, zeitabschnittList.size());

		VerfuegungZeitabschnitt abschnittInBern1 = zeitabschnittList.get(0);
		Assert.assertTrue(abschnittInBern1.isWohnsitzNichtInGemeindeGS1());
		Assert.assertEquals(0, abschnittInBern1.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnittInBern1.getBgPensum());

		VerfuegungZeitabschnitt abschnittInBern2 = zeitabschnittList.get(1);
		Assert.assertFalse(abschnittInBern2.isWohnsitzNichtInGemeindeGS2());
		Assert.assertEquals(100, abschnittInBern2.getAnspruchberechtigtesPensum());
		Assert.assertEquals(100, abschnittInBern2.getBgPensum());

		VerfuegungZeitabschnitt abschnittInBern3 = zeitabschnittList.get(2);
		Assert.assertFalse(abschnittInBern3.isWohnsitzNichtInGemeindeGS2());
		Assert.assertEquals(100, abschnittInBern3.getAnspruchberechtigtesPensum());
		Assert.assertEquals(100, abschnittInBern3.getBgPensum());
	}



	private Betreuung createTestdata(boolean zweigesuchsteller) {
		Betreuung betreuung = TestDataUtil.createGesuchWithBetreuungspensum(zweigesuchsteller);
		betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.KITA);
		betreuung.setBetreuungspensumContainers(new LinkedHashSet<>());
		BetreuungspensumContainer betreuungspensumContainer = new BetreuungspensumContainer();
		betreuungspensumContainer.setBetreuung(betreuung);
		DateRange gueltigkeit = new DateRange(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE);
		betreuungspensumContainer.setBetreuungspensumJA(new Betreuungspensum(gueltigkeit));
		betreuungspensumContainer.getBetreuungspensumJA().setPensum(100);
		betreuung.getBetreuungspensumContainers().add(betreuungspensumContainer);
		betreuung.getKind().getKindJA().setWohnhaftImGleichenHaushalt(100);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, 100, 0));
		if (zweigesuchsteller) {
			betreuung.getKind().getGesuch().getGesuchsteller2().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, 100, 0));
		}
		return betreuung;
	}

	private GesuchstellerAdresseContainer createGesuchstellerAdresse(LocalDate von, LocalDate bis, boolean nichtInGemeinde,
																	 GesuchstellerContainer gesuchsteller) {
		GesuchstellerAdresseContainer adresse = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuchsteller);
		adresse.getGesuchstellerAdresseJA().setNichtInGemeinde(nichtInGemeinde);
		adresse.extractGueltigkeit().setGueltigAb(von);
		adresse.extractGueltigkeit().setGueltigBis(bis);
		return adresse;
	}
}
