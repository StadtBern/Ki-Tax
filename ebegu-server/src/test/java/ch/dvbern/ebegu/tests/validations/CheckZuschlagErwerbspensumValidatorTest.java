package ch.dvbern.ebegu.tests.validations;

import ch.dvbern.ebegu.entities.Erwerbspensum;
import ch.dvbern.ebegu.enums.Zuschlagsgrund;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.validators.CheckZuschlagErwerbspensumZuschlagUndGrund;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import static ch.dvbern.ebegu.tests.util.ValidationTestHelper.assertNotViolated;
import static ch.dvbern.ebegu.tests.util.ValidationTestHelper.assertViolated;

/**
 * Testklasse für Erwerbspensum Validator
 */
public class CheckZuschlagErwerbspensumValidatorTest {

	private ValidatorFactory customFactory;

	@Before
	public void setUp() throws Exception {
		// see https://docs.jboss.org/hibernate/validator/5.2/reference/en-US/html/chapter-bootstrapping.html#_constraintvalidatorfactory
		Configuration<?> config = Validation.byDefaultProvider().configure();
		config.constraintValidatorFactory(new ValidationTestConstraintValidatorFactory(null));
		this.customFactory = config.buildValidatorFactory();
	}

	@Test
	public void testCheckZuschlagPensumUndGrundValidator() {
		Erwerbspensum erwerbspensumData = TestDataUtil.createErwerbspensumData();
		erwerbspensumData.setZuschlagsprozent(null);
		assertViolated(CheckZuschlagErwerbspensumZuschlagUndGrund.class, erwerbspensumData, customFactory, "");

		erwerbspensumData.setZuschlagsprozent(10);
		erwerbspensumData.setZuschlagsgrund(null);
		assertViolated(CheckZuschlagErwerbspensumZuschlagUndGrund.class, erwerbspensumData, customFactory, "");

		erwerbspensumData.setZuschlagsgrund(Zuschlagsgrund.LANGER_ARBWEITSWEG);
		assertNotViolated(CheckZuschlagErwerbspensumZuschlagUndGrund.class, erwerbspensumData, customFactory, "");
	}

}
