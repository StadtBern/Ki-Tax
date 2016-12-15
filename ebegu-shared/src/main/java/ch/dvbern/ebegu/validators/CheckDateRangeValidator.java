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

	/**
	 * gueltigAb und gueltigBis duerfen auch gleich sein. Dies bedeutet eine Zeitspannung von 1 Tag
	 */
	@Override
	public boolean isValid(@Nonnull DateRange instance, ConstraintValidatorContext context) {
		return !instance.getGueltigAb().isAfter(instance.getGueltigBis());
	}
}
