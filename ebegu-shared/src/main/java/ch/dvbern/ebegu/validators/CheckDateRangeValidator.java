package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckDateRangeValidator implements ConstraintValidator<CheckDateRange, DateRange> {

	@Override
	public void initialize(CheckDateRange constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(@Nonnull DateRange instance, ConstraintValidatorContext context) {
		return !instance.getGueltigAb().isBefore(instance.getGueltigBis());
	}
}
