package ch.dvbern.ebegu.tests.validations;

import ch.dvbern.ebegu.services.EbeguParameterService;
import ch.dvbern.ebegu.tests.services.EbeguDummyParameterServiceBean;
import ch.dvbern.ebegu.validators.CheckBetreuungspensumValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validation;

/**
 * This class helps us thest our ConstraintValidators without actually starting a CDI container.
 * Since we are using services inside the validators we need a way to initialize the Validator with a dummy.
 * This Factory allows us to initialize the Validator ourself, giving us the oppurtunity to use a DummyService for the validotr
 */
public class ValidationTestConstraintValidatorFactory implements ConstraintValidatorFactory {

	@Override
	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
		if (key == CheckBetreuungspensumValidator.class) {
			//Mock Service for Parameters
			EbeguParameterService dummyParamService = new EbeguDummyParameterServiceBean();
			return (T) new CheckBetreuungspensumValidator(dummyParamService);
		}
		ConstraintValidatorFactory delegate = Validation.byDefaultProvider().configure().getDefaultConstraintValidatorFactory();
		return delegate.getInstance(key);
	}

	@Override
	public void releaseInstance(ConstraintValidator<?, ?> instance) {
		//nothing to do
	}
}
