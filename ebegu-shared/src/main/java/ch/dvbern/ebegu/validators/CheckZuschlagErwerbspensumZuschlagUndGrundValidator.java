package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.entities.Erwerbspensum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckZuschlagErwerbspensumZuschlagUndGrundValidator implements ConstraintValidator<CheckZuschlagErwerbspensumZuschlagUndGrund, Erwerbspensum> {

	@Override
	public void initialize(CheckZuschlagErwerbspensumZuschlagUndGrund constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Erwerbspensum instance, ConstraintValidatorContext context) {
		if (instance != null) {
			if (instance.getZuschlagZuErwerbspensum()) {
				if (instance.getZuschlagsprozent() == null || instance.getZuschlagsgrund() == null) {
					return false;
				}

			} else {
				if (instance.getZuschlagsprozent() != null || instance.getZuschlagsgrund() != null) {
					return false;
				}
			}
		}
		return true;
	}
}
