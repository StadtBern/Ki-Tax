package ch.dvbern.ebegu.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ch.dvbern.ebegu.entities.Gesuch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Dieser Validator die Komplettheit und Gültigkeit eines Gesuchs
 */
@SuppressWarnings({"ConstantConditions", "PMD.CollapsibleIfStatements"})
public class CheckGesuchCompleteValidator implements ConstraintValidator<CheckGesuchComplete, Gesuch> {

	private static final Logger LOG = LoggerFactory.getLogger(CheckGesuchCompleteValidator.class.getSimpleName());

	@Override
	public void initialize(CheckGesuchComplete constraintAnnotation) {
		//nop
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean isValid(Gesuch gesuch, ConstraintValidatorContext context) {
		boolean valid = true;
		// Familiensituation
		if (gesuch.getFamiliensituationContainer() == null) {
			LOG.error("FamiliensituationContainer is empty for Gesuch {}", gesuch.getId());
			valid = false;
		}
		// Gesuchsteller 1
		if (gesuch.getGesuchsteller1() == null) {
			LOG.error("FamiliensituationContainer is empty for Gesuch {}", gesuch.getId());
			valid = false;
		}
		// Gesuchsteller 2
		if (gesuch.getFamiliensituationContainer().getFamiliensituationJA().hasSecondGesuchsteller()) {
			if (gesuch.getGesuchsteller2() == null ) {
				LOG.error("FamiliensituationContainer is empty for Gesuch {}", gesuch.getId());
				valid = false;
			}
		}
		return valid;
	}
}
