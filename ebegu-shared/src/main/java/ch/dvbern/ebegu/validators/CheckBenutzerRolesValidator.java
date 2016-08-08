package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.enums.UserRole;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Dieser Validator prueft dass die angelegte Benutzer, mit den richtigen Parameter erstellt werden.
 */
public class CheckBenutzerRolesValidator implements ConstraintValidator<CheckBenutzerRoles, Benutzer> {

	@Override
	public void initialize(CheckBenutzerRoles constraintAnnotation) {
		//nop
	}

	/**
	 * Folgende Regeln muessen erfuellt werden:
	 *  - Wenn Rolle=SACHBEARBEITER_INSTITUTION der Benutzer muss mit einer Institution verknuepft werden
	 *  - Wenn Rolle=SACHBEARBEITER_TRAEGERSCHAFT der Benutzer muss mit einer Traegerschaft verknuepft werden
	 *
	 * @param instance Benutzer
	 * @param context context
	 * @return true wenn die Regeln erfuellt sind
	 */
	@Override
	public boolean isValid(Benutzer instance, ConstraintValidatorContext context) {
		if (UserRole.SACHBEARBEITER_INSTITUTION.equals(instance.getRole())) {
			return instance.getInstitution() != null;
		}
		else if (UserRole.SACHBEARBEITER_TRAEGERSCHAFT.equals(instance.getRole())) {
			return instance.getTraegerschaft() != null;
		}

		return true;
	}
}
