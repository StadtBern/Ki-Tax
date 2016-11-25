package ch.dvbern.ebegu.tests.validations;

import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.validators.CheckDateRangeValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Tests fuer CheckDateRangeValidator
 */
public class CheckDateRangeValidatorTest {

	private CheckDateRangeValidator validator;

	@Before
	public void setUp() {
		validator = new CheckDateRangeValidator();
	}

	@Test
	public void testgueltigAbBeforeBis() {
		DateRange dateRange = new DateRange(LocalDate.of(2015, 10, 9), LocalDate.of(2015, 10, 10));
		Assert.assertTrue(validator.isValid(dateRange, null));
	}

	@Test
	public void testgueltigAbEqualsBis() {
		DateRange dateRange = new DateRange(LocalDate.of(2015, 10, 9), LocalDate.of(2015, 10, 9));
		Assert.assertTrue(validator.isValid(dateRange, null));
	}

	@Test
	public void testgueltigAbAfterBis() {
		DateRange dateRange = new DateRange(LocalDate.of(2015, 10, 9), LocalDate.of(2015, 10, 8));
		Assert.assertFalse(validator.isValid(dateRange, null));
	}
}
