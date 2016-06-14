package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungspensum;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.services.EbeguParameterService;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Validator for Betreuungspensen, checks that the entered betreuungspensum is bigger than the minimum
 * that is allowed for the Betreungstyp for a given date
 */
public class CheckBetreuungspensumValidator implements ConstraintValidator<CheckBetreuungspensum, Betreuung> {

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private EbeguParameterService ebeguParameterService;

	@Override
	public void initialize(CheckBetreuungspensum constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Betreuung betreuung, ConstraintValidatorContext context) {
		int index = 0;
		for (BetreuungspensumContainer betPenContainer: betreuung.getBetreuungspensumContainers()) {
			int betreuungsangebotTypMinValue = getMinValueFromBetreuungsangebotTyp(
				betPenContainer.getBetreuungspensumJA().getGueltigkeit().getGueltigAb(),
				betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp());
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
	 * @return The minimum value for the betreuungsangebotTyp. Default value is -1: This means if the given betreuungsangebotTyp doesn't match any
	 * recorded type, the min value will be 0 and any positive value will be then accepted
     */
	private int getMinValueFromBetreuungsangebotTyp(LocalDate stichtag, BetreuungsangebotTyp betreuungsangebotTyp) {
		EbeguParameterKey key = null;
		if (betreuungsangebotTyp == BetreuungsangebotTyp.KITA) {
			key = EbeguParameterKey.PARAM_PENSUM_KITA_MIN;
		}
		else if (betreuungsangebotTyp == BetreuungsangebotTyp.TAGI) {
			key = EbeguParameterKey.PARAM_PENSUM_TAGI_MIN;
		}
		else if (betreuungsangebotTyp == BetreuungsangebotTyp.TAGESSCHULE) {
			key = EbeguParameterKey.PARAM_PENSUM_TAGESSCHULE_MIN;
		}
		else if (betreuungsangebotTyp == BetreuungsangebotTyp.TAGESELTERN) {
			key = EbeguParameterKey.PARAM_PENSUM_TAGESELTERN_MIN;
		}
		if (key != null) {
			Optional<EbeguParameter> parameter = ebeguParameterService.getEbeguParameterByKeyAndDate(key, stichtag);
			if (parameter.isPresent()) {
				return parameter.get().getAsInteger();
			}
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
		if(betreuungspensum != null && betreuungspensum.getPensum() != null && betreuungspensum.getPensum() < pensumMin) {
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
