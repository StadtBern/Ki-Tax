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
