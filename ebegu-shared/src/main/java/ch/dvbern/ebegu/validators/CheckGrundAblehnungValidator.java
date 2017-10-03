package ch.dvbern.ebegu.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import org.apache.commons.lang3.StringUtils;

/**
 * Validator for Betreuungspensen, checks that a grundAblehnung exists if the Status is set to
 */
public class CheckGrundAblehnungValidator implements ConstraintValidator<CheckGrundAblehnung, Betreuung> {

	@Override
	public void initialize(CheckGrundAblehnung constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Betreuung instance, ConstraintValidatorContext context) {
		return !Betreuungsstatus.ABGEWIESEN.equals(instance.getBetreuungsstatus())
			|| instance.getGrundAblehnung() != null && !StringUtils.isEmpty(instance.getGrundAblehnung());
	}
}
