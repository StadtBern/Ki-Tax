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
 * Je nach {@link ch.dvbern.ebegu.enums.BetreuungsangebotTyp} der verknuepften Institutionstammdaten darf im Betreuungspensum nur eingeschraenkte
 * Werte eingegeben werden.
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckBetreuungspensumDatesOverlappingValidator.class)
@Documented
public @interface CheckBetreuungspensumDatesOverlapping {

	String message() default "{invalid_betreuungspensen_dates}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
