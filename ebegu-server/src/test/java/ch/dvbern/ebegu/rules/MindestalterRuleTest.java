package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static ch.dvbern.ebegu.rules.EbeguRuleTestsHelper.calculate;

/**
 * Tests für MindestalterRule
 */
public class MindestalterRuleTest {

	@Test
	public void testKindGenugAlt() {
		Betreuung betreuung = createTestData(LocalDate.of(2015, Month.MAY, 15), TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE);
		final Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		List<VerfuegungZeitabschnitt> zeitabschnittList = calculate(betreuung);

		Assert.assertNotNull(zeitabschnittList);
		Assert.assertEquals(1, zeitabschnittList.size());
		VerfuegungZeitabschnitt abschnittAltGenug = zeitabschnittList.get(0);
		Assert.assertFalse(abschnittAltGenug.isKindMinestalterUnterschritten());
		Assert.assertEquals(100, abschnittAltGenug.getAnspruchberechtigtesPensum());
		Assert.assertEquals(100, abschnittAltGenug.getBgPensum());
	}

	@Test
	public void testKindAnfangsZuJung() {
		Betreuung betreuung = createTestData(LocalDate.of(TestDataUtil.PERIODE_JAHR_1, Month.JULY, 15), TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE);
		final Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		List<VerfuegungZeitabschnitt> zeitabschnittList = calculate(betreuung);

		Assert.assertNotNull(zeitabschnittList);
		Assert.assertEquals(2, zeitabschnittList.size());
		VerfuegungZeitabschnitt abschnittZuJung = zeitabschnittList.get(0);
		Assert.assertTrue(abschnittZuJung.isKindMinestalterUnterschritten());
		Assert.assertEquals(0, abschnittZuJung.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnittZuJung.getBgPensum());
		VerfuegungZeitabschnitt abschnittAltGenug = zeitabschnittList.get(1);
		Assert.assertFalse(abschnittAltGenug.isKindMinestalterUnterschritten());
		Assert.assertEquals(100, abschnittAltGenug.getAnspruchberechtigtesPensum());
		Assert.assertEquals(100, abschnittAltGenug.getBgPensum());
	}

	private Betreuung createTestData(LocalDate geburtsdatum, LocalDate betreuungVon, LocalDate betreuungBis) {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(betreuungVon, betreuungBis,  BetreuungsangebotTyp.KITA, 100);
		betreuung.getKind().getKindJA().setGeburtsdatum(geburtsdatum);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, 100, 0));
		return betreuung;
	}
}
