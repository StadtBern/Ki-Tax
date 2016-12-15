package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.entities.Erwerbspensum;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Wenn das {@link Erwerbspensum}#zuschlagZuErwerbspensum flag nicht gesetzt ist dann muss ein Zuschlagspensum gesetzt sein. Ansonsten soll nichts
 * gesetzt sein
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckZuschlagErwerbspensumZuschlagUndGrundValidator.class)
@Documented
public @interface CheckZuschlagErwerbspensumZuschlagUndGrund {

	String message() default "{invalid_zuschlag_zu_erwerbspensum}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
