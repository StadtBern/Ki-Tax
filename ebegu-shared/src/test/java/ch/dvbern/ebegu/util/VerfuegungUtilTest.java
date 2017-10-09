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

package ch.dvbern.ebegu.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests fuer VerfuegungUtil
 */
public class VerfuegungUtilTest {

	/**
	 * J   F   M   A   M   J   J
	 * |---|---|---|---|---|---|   -> Zeitabschnitte
	 * |---|                   -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitInterval1Abschnitt() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 31));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(80).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 * J   F   M   A   M   J   J
	 * |---|---|---|---|---|---|   -> Zeitabschnitte
	 * |-------|               -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitInterval2Abschnitte() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 4, 30));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(160).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 * J   F   M   A   M   J   J
	 * |---|---|---|---|---|---|   -> Zeitabschnitte
	 * |-|                    -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitIntervalSubabschnitt() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 3, 10), LocalDate.of(2018, 3, 25));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(41.29).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 * J   F   M   A   M   J   J
	 * |---|---|---|---|---|---|   -> Zeitabschnitte
	 * |-----|                 -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitIntervalOnAndHalfabschnitt() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 4, 20));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(133.33).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 * J   F   M   A   M   J   J
	 * |---|---|---|---|---|---|   -> Zeitabschnitte
	 * |---|                 -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitIntervalTwoHalfsabschnitt() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 3, 15), LocalDate.of(2018, 4, 20));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(97.20).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 * J   F   M   A   M   J   J
	 * |---|---|---|---|---|---|   -> Zeitabschnitte
	 * |----|               -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitIntervalTwoHalfsabschnitt2() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 3, 15), LocalDate.of(2018, 4, 30));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(123.87).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 * J   F   M   A   M   J   J
	 * |---|---|---|---|---|---|   -> Zeitabschnitte
	 * |-----------|         -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitIntervalSeveralabschnitte() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 2, 27), LocalDate.of(2018, 5, 3));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(173.46).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 * J   F   M   A   M   J   J
	 * |---|---|---|---|---|---|   -> Zeitabschnitte
	 * |---|                               -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitIntervalOutOfRange() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2017, 12, 10), LocalDate.of(2017, 12, 30));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	// HELP METHODS

	@Nonnull
	private DateRange createIntervall(LocalDate dateAb, LocalDate dateBis) {
		DateRange dateRange = new DateRange();
		dateRange.setGueltigAb(dateAb);
		dateRange.setGueltigBis(dateBis);
		return dateRange;
	}

	@Nonnull
	private List<VerfuegungZeitabschnitt> createZeitabschnitte() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();
		for (int i = 1; i < 8; i++) {
			VerfuegungZeitabschnitt abschnitt1 = new VerfuegungZeitabschnitt();
			// ranges for whole months. Last day calculated from next month minus 1 day
			DateRange gueltigkeit = createIntervall(LocalDate.of(2018, i, 1), LocalDate.of(2018, i + 1, 1).minusDays(1));
			abschnitt1.setGueltigkeit(gueltigkeit);
			abschnitt1.setVollkosten(new BigDecimal(100));
			abschnitt1.setElternbeitrag(new BigDecimal(20));
			zeitabschnitte.add(abschnitt1);
		}
		return zeitabschnitte;
	}
}
