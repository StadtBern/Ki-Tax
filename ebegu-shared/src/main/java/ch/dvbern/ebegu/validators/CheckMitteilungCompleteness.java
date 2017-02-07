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
 * Wenn eine Mitteilung mit Status != ENTWURF gespeichert wird, muss diese Text in subject und message haben.
 * Eine Mitteilung mit Status ENTWURF ist immer valid
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckMitteilungCompletenessValidator.class)
@Documented
public @interface CheckMitteilungCompleteness {

	String message() default "{invalid_mitteilung_not_complete}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
