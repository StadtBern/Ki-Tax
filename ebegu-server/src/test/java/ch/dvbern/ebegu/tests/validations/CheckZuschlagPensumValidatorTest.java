package ch.dvbern.ebegu.tests.validations;

import ch.dvbern.ebegu.entities.Erwerbspensum;
import ch.dvbern.ebegu.enums.Zuschlagsgrund;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.validators.CheckZuschlagPensum;
import org.junit.Test;

import static ch.dvbern.lib.beanvalidation.util.ValidationTestHelper.assertNotViolated;
import static ch.dvbern.lib.beanvalidation.util.ValidationTestHelper.assertViolated;

/**
 * Testklasse für Erwerbspensum Validator
 */
public class CheckZuschlagPensumValidatorTest {

	@Test
	public void testCheckZuschlagPensumValidator() {
		Erwerbspensum erwerbspensumData = TestDataUtil.createErwerbspensumData();
		erwerbspensumData.setZuschlagsprozent(null);
		assertViolated(CheckZuschlagPensum.class, erwerbspensumData, "");

		erwerbspensumData.setZuschlagsprozent(10);
		erwerbspensumData.setZuschlagsgrund(null);
		assertViolated(CheckZuschlagPensum.class, erwerbspensumData, "");

		erwerbspensumData.setZuschlagsgrund(Zuschlagsgrund.LANGER_ARBWEITSWEG);
		assertNotViolated(CheckZuschlagPensum.class, erwerbspensumData, "");

	}
}
