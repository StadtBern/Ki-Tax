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

package ch.dvbern.ebegu.tests.vorlagen;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerAdresseContainer;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.vorlagen.PrintUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test fuer PrintUtil
 */
public class PrintUtilTest {

	@Test
	public void testGetGesuchstellerAdresseWithNoAdressen() {
		final Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		gesuchsteller.setAdressen(new ArrayList<>());

		final Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertFalse(gesuchstellerAdresse.isPresent());
	}

	@Test
	public void testGetGesuchstellerAdresseWithJustWohnadresse() {
		final Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);

		final Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertTrue(gesuchstellerAdresse.isPresent());
		Assert.assertEquals(gesuchsteller.getAdressen().get(0), gesuchstellerAdresse.get());
	}

	@Test
	public void testGetGesuchstellerAdresseWithUmzugsadresse() {
		final Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		final GesuchstellerAdresseContainer umzugsadresse = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuchsteller);
		umzugsadresse.getGesuchstellerAdresseJA().setStrasse("newStrasse");
		gesuchsteller.addAdresse(umzugsadresse);

		//update Gueltigkeiten
		final LocalDate now = LocalDate.now();
		gesuchsteller.getAdressen().get(0).getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(now.minusMonths(5), now.minusDays(1))); // before now
		gesuchsteller.getAdressen().get(1).getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(now, now.plusMonths(2))); // now liegt in dieser Periode

		final Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertTrue(gesuchstellerAdresse.isPresent());
		Assert.assertEquals(gesuchsteller.getAdressen().get(1), gesuchstellerAdresse.get());
		Assert.assertEquals("newStrasse", gesuchstellerAdresse.get().extractStrasse());
	}

	@Test
	public void testGetGesuchstellerAdresseWithKorrespondezadresse() {
		final Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		final GesuchstellerAdresseContainer korrespondenzadresse = createKorrespondenzadresse(gesuchsteller);

		final Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertTrue(gesuchstellerAdresse.isPresent());
		Assert.assertEquals(korrespondenzadresse, gesuchstellerAdresse.get());
		Assert.assertEquals("korrespondezStrasse", gesuchstellerAdresse.get().extractStrasse());
	}

	@Test
	public void testGetGesuchstellerAdresseWithKorrespondezadresseDeletedByJA() {
		final Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		//wir haben eine korrespondenzadresse aber diese wurde vom JA weggenommen
		final GesuchstellerAdresseContainer korrespondenzadresse = createKorrespondenzadresse(gesuchsteller);
		korrespondenzadresse.setGesuchstellerAdresseJA(null);

		final Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertTrue(gesuchstellerAdresse.isPresent());
		Assert.assertEquals(gesuchsteller.getAdressen().get(0), gesuchstellerAdresse.get());
		Assert.assertEquals(AdresseTyp.WOHNADRESSE, gesuchstellerAdresse.get().extractAdresseTyp());
		Assert.assertEquals("Nussbaumstrasse", gesuchstellerAdresse.get().extractStrasse());
	}

	@Test
	public void testGetGesuchstellerAdresseWithKorrespondezadresseAndUmzugsadresse() {
		final Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		final GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		final GesuchstellerAdresseContainer korrespondenzadresse = createKorrespondenzadresse(gesuchsteller);
		final GesuchstellerAdresseContainer umzugsadresse = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuchsteller);
		umzugsadresse.getGesuchstellerAdresseJA().setStrasse("newStrasse");
		gesuchsteller.addAdresse(umzugsadresse);

		//update Gueltigkeiten
		final LocalDate now = LocalDate.now();
		final List<GesuchstellerAdresseContainer> wohnAdressen = gesuchsteller.getAdressen().stream()
			.filter(gesuchstellerAdresse -> !gesuchstellerAdresse.extractIsKorrespondenzAdresse())
			.sorted(Comparator.comparing(o -> o.extractGueltigkeit().getGueltigAb()))
			.collect(Collectors.toList());
		wohnAdressen.get(0).getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(now.minusMonths(5), now.minusDays(1))); // before now
		wohnAdressen.get(1).getGesuchstellerAdresseJA().setGueltigkeit(new DateRange(now, now.plusMonths(2))); // now liegt in dieser Periode

		final Optional<GesuchstellerAdresseContainer> gesuchstellerAdresse = PrintUtil.getGesuchstellerAdresse(gesuchsteller);

		Assert.assertTrue(gesuchstellerAdresse.isPresent());
		Assert.assertEquals(korrespondenzadresse, gesuchstellerAdresse.get());
		Assert.assertEquals("korrespondezStrasse", gesuchstellerAdresse.get().extractStrasse());
	}

	@Nonnull
	private GesuchstellerAdresseContainer createKorrespondenzadresse(GesuchstellerContainer gesuchsteller) {
		final GesuchstellerAdresseContainer korrespondenzadresse = TestDataUtil.createDefaultGesuchstellerAdresseContainer(gesuchsteller);
		korrespondenzadresse.getGesuchstellerAdresseJA().setStrasse("korrespondezStrasse");
		korrespondenzadresse.getGesuchstellerAdresseJA().setAdresseTyp(AdresseTyp.KORRESPONDENZADRESSE);
		gesuchsteller.addAdresse(korrespondenzadresse);
		return korrespondenzadresse;
	}
}
