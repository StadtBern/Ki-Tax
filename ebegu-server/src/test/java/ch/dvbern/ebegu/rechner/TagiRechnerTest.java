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

package ch.dvbern.ebegu.rechner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testet den Tagi-Rechner
 */
public class TagiRechnerTest extends AbstractBGRechnerTest {

	private final BGRechnerParameterDTO parameterDTO = getParameter();
	private final TagiRechner tagiRechner = new TagiRechner();

	@Test
	public void testEinTagHohesEinkommenAnspruch15() {
		Verfuegung verfuegung = prepareVerfuegungTagiUndTageseltern(
			LocalDate.of(2016, Month.JANUARY, 21), LocalDate.of(2016, Month.JANUARY, 21),
			15, new BigDecimal("234567"));

		VerfuegungZeitabschnitt calculate = tagiRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("11.90"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("11.90"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("0.00"), calculate.getVerguenstigung());
	}

	@Test
	public void testTeilmonatMittleresEinkommen() {
		Verfuegung verfuegung = prepareVerfuegungTagiUndTageseltern(
			LocalDate.of(2016, Month.JANUARY, 21), LocalDate.of(2016, Month.JANUARY, 31),
			100, new BigDecimal("87654"));

		VerfuegungZeitabschnitt calculate = tagiRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("555.80"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("237.30"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("318.50"), calculate.getVerguenstigung());
	}

	@Test
	public void testTeilmonatMittleresEinkommen50() {
		Verfuegung verfuegung = prepareVerfuegungTagiUndTageseltern(
			LocalDate.of(2016, Month.JANUARY, 21), LocalDate.of(2016, Month.JANUARY, 31),
			50, new BigDecimal("87654"));

		VerfuegungZeitabschnitt calculate = tagiRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("277.90"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("118.65"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("159.25"), calculate.getVerguenstigung());
	}

	@Test
	public void testGanzerMonatZuWenigEinkommen() {
		Verfuegung verfuegung = prepareVerfuegungTagiUndTageseltern(
			LocalDate.of(2016, Month.JANUARY, 1), LocalDate.of(2016, Month.JANUARY, 31),
			100, new BigDecimal("27750"));

		VerfuegungZeitabschnitt calculate = tagiRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
		Assert.assertEquals(new BigDecimal("1667.40"), calculate.getVollkosten());
		Assert.assertEquals(new BigDecimal("105.00"), calculate.getElternbeitrag());
		Assert.assertEquals(new BigDecimal("1562.40"), calculate.getVerguenstigung());
	}

	@Test (expected = IllegalArgumentException.class)
	public void testZeitraumUeberMonatsende() {
		Verfuegung verfuegung = prepareVerfuegungTagiUndTageseltern(
			LocalDate.of(2016, Month.JANUARY, 10), LocalDate.of(2016, Month.FEBRUARY, 5),
			100, new BigDecimal("27750"));

		tagiRechner.calculate(verfuegung.getZeitabschnitte().get(0), verfuegung, parameterDTO);
	}
}
