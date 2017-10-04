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

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.junit.Assert;
import org.junit.Test;

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
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(betreuungVon, betreuungBis, BetreuungsangebotTyp.KITA, 100);
		betreuung.getKind().getKindJA().setGeburtsdatum(geburtsdatum);
		betreuung.getKind().getGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE, 100, 0));
		return betreuung;
	}
}
