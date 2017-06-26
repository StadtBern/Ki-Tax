package ch.dvbern.ebegu.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ch.dvbern.ebegu.entities.FinanzielleSituationContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Dieser Validator die Komplettheit und Gültigkeit eines FinanzielleSituationContainer
 */
public class CheckFinanzielleSituationContainerCompleteValidator implements
	ConstraintValidator<CheckFinanzielleSituationContainerComplete, FinanzielleSituationContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(CheckFinanzielleSituationContainerCompleteValidator.class.getSimpleName());

	@Override
	public void initialize(CheckFinanzielleSituationContainerComplete constraintAnnotation) {
		//nop
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean isValid(FinanzielleSituationContainer finSitContainer, ConstraintValidatorContext context) {
		boolean valid = true;
		if (finSitContainer.getFinanzielleSituationJA() == null) {
			LOG.error("FinanzielleSituationJA is empty for FinanzielleSituationContainer {}", finSitContainer.getId());
			valid = false;
		}
		if (finSitContainer.getGesuchsteller() == null) {
			LOG.error("GesuchstellerContainer is empty for FinanzielleSituationContainer {}", finSitContainer.getId());
			valid = false;
		}
		return valid;
	}
}
