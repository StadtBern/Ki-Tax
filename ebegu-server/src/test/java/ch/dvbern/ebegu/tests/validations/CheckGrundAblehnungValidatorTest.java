package ch.dvbern.ebegu.tests.validations;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.validators.CheckGrundAblehnungValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests fuer CheckGrundAblehnungValidator
 */
public class CheckGrundAblehnungValidatorTest {

	private CheckGrundAblehnungValidator validator;
	private Betreuung betreuung;

	@Before
	public void setUp() throws Exception {
		validator = new CheckGrundAblehnungValidator();
		betreuung = TestDataUtil.createDefaultBetreuung();
	}

	@Test
	public void testNichtAbgewiesenEmptyGrund() {
		betreuung.setGrundAblehnung("");
		Assert.assertTrue(validator.isValid(betreuung, null));
	}

	@Test
	public void testNichtAbgewiesenNullGrund() {
		betreuung.setGrundAblehnung(null);
		Assert.assertTrue(validator.isValid(betreuung, null));
	}

	@Test
	public void testNichtAbgewiesenWithGrund() {
		betreuung.setGrundAblehnung("mein Grund");
		Assert.assertTrue(validator.isValid(betreuung, null));
	}

	@Test
	public void testAbgewiesenEmptyGrund() {
		betreuung.setBetreuungsstatus(Betreuungsstatus.ABGEWIESEN);
		betreuung.setGrundAblehnung("");
		Assert.assertFalse(validator.isValid(betreuung, null));
	}

	@Test
	public void testAbgewiesenNullGrund() {
		betreuung.setBetreuungsstatus(Betreuungsstatus.ABGEWIESEN);
		betreuung.setGrundAblehnung(null);
		Assert.assertFalse(validator.isValid(betreuung, null));
	}

	@Test
	public void testAbgewiesenWithGrund() {
		betreuung.setBetreuungsstatus(Betreuungsstatus.ABGEWIESEN);
		betreuung.setGrundAblehnung("mein Grund");
		Assert.assertTrue(validator.isValid(betreuung, null));
	}

}
