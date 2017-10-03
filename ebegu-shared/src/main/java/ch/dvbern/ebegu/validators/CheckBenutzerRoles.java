package ch.dvbern.ebegu.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckBenutzerRolesValidator.class)
@Documented
public @interface CheckBenutzerRoles {

	String message() default "{invalid_benutzer_roles_rules}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
