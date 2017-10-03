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

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Tests fuer DateUtil
 */
public class DateUtilTest {

	@Test
	public void parseStringToDateOrReturnNowTestNullString() {
		final LocalDate now = LocalDate.now();
		final LocalDate returnedDate = DateUtil.parseStringToDateOrReturnNow(null);
		Assert.assertNotNull(returnedDate);
		Assert.assertTrue(now.isEqual(returnedDate));
	}

	@Test
	public void parseStringToDateOrReturnNowTestEmptyString() {
		final LocalDate now = LocalDate.now();
		final LocalDate returnedDate = DateUtil.parseStringToDateOrReturnNow("");
		Assert.assertNotNull(returnedDate);
		Assert.assertTrue(now.isEqual(returnedDate));
	}

	@Test
	public void parseStringToDateOrReturnNowTest() {
		final LocalDate now = LocalDate.now();
		final LocalDate returnedDate = DateUtil.parseStringToDateOrReturnNow("2015-12-31");
		Assert.assertNotNull(returnedDate);
		Assert.assertEquals(2015, returnedDate.getYear());
		Assert.assertEquals(12, returnedDate.getMonthValue());
		Assert.assertEquals(31, returnedDate.getDayOfMonth());
	}
}
