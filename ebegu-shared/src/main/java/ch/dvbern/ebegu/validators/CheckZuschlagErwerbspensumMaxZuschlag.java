package ch.dvbern.ebegu.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validator fuer den Maximalen Zuschlag zum Erwerbspensum
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckZuschlagErwerbspensumMaxZuschlagValidator.class)
@Documented
public @interface CheckZuschlagErwerbspensumMaxZuschlag {

	String message() default "{invalid_erwerbspensum_zuschlag}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
