package ch.dvbern.ebegu.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ch.dvbern.ebegu.entities.FamiliensituationContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dieser Validator die Komplettheit und Gültigkeit eines FamiliensituationContainer
 */
public class CheckFamiliensituationContainerCompleteValidator implements
	ConstraintValidator<CheckFamiliensituationContainerComplete, FamiliensituationContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(CheckFamiliensituationContainerCompleteValidator.class.getSimpleName());

	@Override
	public void initialize(CheckFamiliensituationContainerComplete constraintAnnotation) {
		//nop
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean isValid(FamiliensituationContainer famSitContainer, ConstraintValidatorContext context) {
		boolean valid = true;
		if (famSitContainer.getFamiliensituationJA() == null) {
			LOG.error("FamiliensituationJA is empty for FamiliensituationContainer {}", famSitContainer.getId());
			valid = false;
		}
		return valid;
	}
}
