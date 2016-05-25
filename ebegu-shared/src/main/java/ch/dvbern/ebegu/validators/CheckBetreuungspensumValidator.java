package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungspensum;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator fuer Betreuungspensen
 */
public class CheckBetreuungspensumValidator implements ConstraintValidator<CheckBetreuungspensum, Betreuung> {

	//todo team Diese Konstanten durch Werte aus der DB ersetzen. siehe todo unten
	private static int KITA_PENSUM_MIN = 10;
	private static int TAGI_PENSUM_MIN = 60;
	private static int TAGESSCHULE_PENSUM_MIN = 20;
	private static int TAGESELTERN_PENSUM_MIN = 0;

	@Override
	public void initialize(CheckBetreuungspensum constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Betreuung instance, ConstraintValidatorContext context) {
		// todo team Fuer diese Methode muessen wir die Werte aus der DB holen. Das koennen wir erst machen wenn
		// die neue ApplicationProperties fuer die Periode implementiert ist. Momentan werden diese Werte direkt
		// hier als Constants kodiert.


//		if (instance == null || instance.getInstitutionStammdaten() == null || instance.getInstitutionStammdaten().getBetreuungsangebotTyp() == null) {
//			return true;
//		}

		BetreuungsangebotTyp betreuungsangebotTyp = instance.getInstitutionStammdaten().getBetreuungsangebotTyp();

		boolean yo = instance.getBetreuungspensumContainers().stream().allMatch(container ->
			(validateBetreuungspensum(betreuungsangebotTyp, container.getBetreuungspensumGS())
				&& validateBetreuungspensum(betreuungsangebotTyp, container.getBetreuungspensumJA())));

		return yo;
	}

	private boolean validateBetreuungspensum(BetreuungsangebotTyp betreuungsangebotTyp, Betreuungspensum betreuungspensum) {
		if (betreuungspensum != null) {
			if (betreuungsangebotTyp == BetreuungsangebotTyp.KITA) {
				return !(betreuungspensum.getPensum() < KITA_PENSUM_MIN || betreuungspensum.getPensum() > 100);
			}
			if (betreuungsangebotTyp == BetreuungsangebotTyp.TAGI) {
				return !(betreuungspensum.getPensum() < TAGI_PENSUM_MIN || betreuungspensum.getPensum() > 100);
			}
			if (betreuungsangebotTyp == BetreuungsangebotTyp.TAGESSCHULE) {
				return !(betreuungspensum.getPensum() < TAGESSCHULE_PENSUM_MIN || betreuungspensum.getPensum() > 100);
			}
			if (betreuungsangebotTyp == BetreuungsangebotTyp.TAGESELTERN) {
				return !(betreuungspensum.getPensum() < TAGESELTERN_PENSUM_MIN || betreuungspensum.getPensum() > 100);
			}
		}
		return true;
	}
}
