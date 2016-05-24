package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.entities.Betreuungspensum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator fuer Betreuungspensen
 */
public class CheckBetreuungspensumValidator implements ConstraintValidator<CheckBetreuungspensum, Betreuungspensum> {

	@Override
	public void initialize(CheckBetreuungspensum constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Betreuungspensum instance, ConstraintValidatorContext context) {
//		if (instance != null) {
//			if (instance.getZuschlagZuErwerbspensum()) {
//				if (instance.getZuschlagsprozent() == null || instance.getZuschlagsgrund() == null) {
//					return false;
//				}
//
//			} else {
//				if (instance.getZuschlagsprozent() != null || instance.getZuschlagsgrund() != null) {
//					return false;
//				}
//			}
//
//		}
		return true;
	}
}
