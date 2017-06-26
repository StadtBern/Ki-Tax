package ch.dvbern.ebegu.tests.validations;

import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.enums.MitteilungStatus;
import ch.dvbern.ebegu.validators.CheckMitteilungCompletenessValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests fuer {@link CheckMitteilungCompletenessValidator}
 */
public class CheckMitteilungCompletenessValidatorTest {

	CheckMitteilungCompletenessValidator validator;

	@Before
	public void setUp() {
		validator = new CheckMitteilungCompletenessValidator();
	}

	@Test
	public void testEntwurf() {
		Mitteilung mitteilung = new Mitteilung();
		mitteilung.setMitteilungStatus(MitteilungStatus.ENTWURF);
		Assert.assertTrue(validator.isValid(mitteilung, null));
	}

	@Test
	public void testNeuNoMessageNoSubject() {
		Mitteilung mitteilung = new Mitteilung();
		mitteilung.setMitteilungStatus(MitteilungStatus.NEU);
		Assert.assertFalse(validator.isValid(mitteilung, null));
	}

	@Test
	public void testNeuNoMessage() {
		Mitteilung mitteilung = new Mitteilung();
		mitteilung.setMitteilungStatus(MitteilungStatus.NEU);
		mitteilung.setSubject("subject");
		Assert.assertFalse(validator.isValid(mitteilung, null));
	}

	@Test
	public void testNeuNoSubject() {
		Mitteilung mitteilung = new Mitteilung();
		mitteilung.setMitteilungStatus(MitteilungStatus.NEU);
		mitteilung.setMessage("message");
		Assert.assertFalse(validator.isValid(mitteilung, null));
	}

	@Test
	public void testNeuComplete() {
		Mitteilung mitteilung = new Mitteilung();
		mitteilung.setMitteilungStatus(MitteilungStatus.NEU);
		mitteilung.setSubject("subject");
		mitteilung.setMessage("message");
		Assert.assertTrue(validator.isValid(mitteilung, null));
	}

}
