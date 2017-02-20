package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.entities.Betreuungsmitteilung;
import ch.dvbern.ebegu.entities.BetreuungsmitteilungPensum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

/**
 * Validator fuer Datum in Betreuungspensen. Die Zeitraeume duerfen sich nicht ueberschneiden
 */
public class CheckBetreuungsmitteilungDatesOverlappingValidator implements ConstraintValidator<CheckBetreuungsmitteilungDatesOverlapping, Betreuungsmitteilung> {
	@Override
	public void initialize(CheckBetreuungsmitteilungDatesOverlapping constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Betreuungsmitteilung instance, ConstraintValidatorContext context) {
		return !checkOverlapping(instance.getBetreuungspensen());
	}

	/**
	 * prueft ob es eine ueberschneidung zwischen den Zeitrauemen gibt
	 * @param betMitteilungPensum
	 */
	private boolean checkOverlapping(Set<BetreuungsmitteilungPensum> betMitteilungPensum) {
		return betMitteilungPensum.stream()
			.anyMatch(o1 -> betMitteilungPensum.stream()
				.anyMatch(o2 -> !o1.equals(o2) && o1.getGueltigkeit().intersects(o2.getGueltigkeit())));
	}
}
