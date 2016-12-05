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
 * Die Abwesenheiten einer Betreuung duerfen sich nicht ueberlappen
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckAbwesenheitDatesOverlappingValidator.class)
@Documented
public @interface CheckAbwesenheitDatesOverlapping {

	String message() default "{invalid_abwesenheiten_dates}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
