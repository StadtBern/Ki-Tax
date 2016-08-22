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
 * Wenn der Status ABGEWIESEN ist, muss unbedingt ein ablehnungGrund eingegeben werden
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckGrundAblehnungValidator.class)
@Documented
public @interface CheckGrundAblehnung {

	String message() default "{invalid_grund_ablehnung}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
