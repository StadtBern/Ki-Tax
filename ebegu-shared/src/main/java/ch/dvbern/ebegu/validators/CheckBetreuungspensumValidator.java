package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungspensum;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Validator fuer Betreuungspensen
 */
public class CheckBetreuungspensumValidator implements ConstraintValidator<CheckBetreuungspensum, Betreuung> {

	//todo team Diese Konstanten durch Werte aus der DB ersetzen. siehe todo unten
	private static final int KITA_PENSUM_MIN = 10;
	private static final int TAGI_PENSUM_MIN = 60;
	private static final int TAGESSCHULE_PENSUM_MIN = 0;
	private static final int TAGESELTERN_PENSUM_MIN = 20;

	@Override
	public void initialize(CheckBetreuungspensum constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Betreuung betreuung, ConstraintValidatorContext context) {
		// todo team Fuer diese Methode muessen wir die Werte aus der DB holen. Das koennen wir erst machen wenn
		// die neue ApplicationProperties fuer die Periode implementiert ist. Momentan werden diese Werte direkt
		// hier als Constants kodiert.
		int betreuungsangebotTypMinValue = getMinValueFromBetreuungsangebotTyp(betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp());

		int index = 0;
		for (BetreuungspensumContainer betPenContainer: betreuung.getBetreuungspensumContainers()) {
			if (!validateBetreuungspensum(betPenContainer.getBetreuungspensumGS(), betreuungsangebotTypMinValue, index, "GS", context)
				|| !validateBetreuungspensum(betPenContainer.getBetreuungspensumJA(), betreuungsangebotTypMinValue, index, "JA", context)) {
				return false;
			}
			index++;
		}

		return true;
	}

	/**
	 * Returns the corresponding minimum value for the given betreuungsangebotTyp.
	 * @param betreuungsangebotTyp betreuungsangebotTyp
	 * @return The minimum value for the betreuungsangebotTyp. Default value is -1: This means if the given betreuungsangebotTyp doesn't much any
	 * recorded type, the min value will be 0 and any positive value will be then accepted
     */
	private int getMinValueFromBetreuungsangebotTyp(BetreuungsangebotTyp betreuungsangebotTyp) {
		if (betreuungsangebotTyp == BetreuungsangebotTyp.KITA) {
			return KITA_PENSUM_MIN;
		}
		if (betreuungsangebotTyp == BetreuungsangebotTyp.TAGI) {
			return TAGI_PENSUM_MIN;
		}
		if (betreuungsangebotTyp == BetreuungsangebotTyp.TAGESSCHULE) {
			return TAGESSCHULE_PENSUM_MIN;
		}
		if (betreuungsangebotTyp == BetreuungsangebotTyp.TAGESELTERN) {
			return TAGESELTERN_PENSUM_MIN;
		}
		return 0;
	}

	/**
	 * With the given the pensumMin it checks if the introduced pensum is in the permitted range. Case not a ConstraintValidator will be created
	 * with a message and a path indicating which object threw the error. False will be returned in the explained case. In case the value for pensum
	 * is right, nothing will be done and true will be returned.
	 * @param betreuungspensum the betreuungspensum to check
	 * @param pensumMin the minimum permitted value for pensum
	 * @param index the index of the Betreuungspensum inside the betreuungspensum container
	 * @param objectType JA or GS
	 * @param context the context
     * @return true if the value resides inside the permitted range. False otherwise
     */
	private boolean validateBetreuungspensum(Betreuungspensum betreuungspensum, int pensumMin, int index, String objectType, ConstraintValidatorContext context) {
		// todo homa in Review. Es waere moeglich, die Messages mit der Klasse HibernateConstraintValidatorContext zu erzeugen. Das waere aber Hibernate-abhaengig. wuerde es Sinn machen??
		if(betreuungspensum != null && betreuungspensum.getPensum() < pensumMin) {
			ResourceBundle rb = ResourceBundle.getBundle("ValidationMessages");
			String message = rb.getString("invalid_betreuungspensum");
			message = MessageFormat.format(message, pensumMin);

			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(message)
				.addPropertyNode("betreuungspensumContainers[" + Integer.toString(index) + "].betreuungspensum" + objectType + ".pensum")
				.addConstraintViolation();

			return false;
		}
		return true;
	}
}
