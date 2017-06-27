package ch.dvbern.ebegu.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Dieser Validator die Komplettheit und Gültigkeit eines GesuchstellerContainers
 */
public class CheckGesuchstellerContainerCompleteValidator implements
	ConstraintValidator<CheckGesuchstellerContainerComplete, GesuchstellerContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(CheckGesuchstellerContainerCompleteValidator.class.getSimpleName());

	@Override
	public void initialize(CheckGesuchstellerContainerComplete constraintAnnotation) {
		//nop
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean isValid(GesuchstellerContainer gsContainer, ConstraintValidatorContext context) {
		boolean valid = true;
		if (gsContainer.getGesuchstellerJA() == null) {
			LOG.error("GesuchstellerJA is empty for GesuchstellerContainer {}", gsContainer.getId());
			valid = false;
		}
		if (gsContainer.getFinanzielleSituationContainer() == null) {
			LOG.error("FinanzielleSituationContainer is empty for GesuchstellerContainer {}", gsContainer.getId());
			valid = false;
		}
		return valid;
	}
}
