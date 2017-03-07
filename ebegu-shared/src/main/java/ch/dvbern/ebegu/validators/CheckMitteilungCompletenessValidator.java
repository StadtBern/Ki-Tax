package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.entities.Mitteilung;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator fuer Mitteilungen
 */
public class CheckMitteilungCompletenessValidator implements ConstraintValidator<CheckMitteilungCompleteness, Mitteilung> {

	@Override
	public void initialize(CheckMitteilungCompleteness constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Mitteilung instance, ConstraintValidatorContext context) {
		return instance.isEntwurf()
			|| (instance.getMessage() != null && !instance.getMessage().isEmpty()
			&& instance.getSubject() != null && !instance.getSubject().isEmpty());
	}
}
