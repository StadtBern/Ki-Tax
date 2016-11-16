package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungspensum;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validator fuer Datum in Betreuungspensen. Die Zeitraeume duerfen sich nicht ueberschneiden
 */
public class CheckBetreuungspensumDatesOverlappingValidator implements ConstraintValidator<CheckBetreuungspensumDatesOverlapping, Betreuung> {
	@Override
	public void initialize(CheckBetreuungspensumDatesOverlapping constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Betreuung instance, ConstraintValidatorContext context) {
		return !(checkOverlapping("JA", instance.getBetreuungspensumContainers()) || checkOverlapping("GS", instance.getBetreuungspensumContainers()));
	}

	/**
	 * prueft ob es eine ueberschneidung zwischen den Zeitrauemen gibt
	 */
	private boolean checkOverlapping(String type, Set<BetreuungspensumContainer> betreuungspensumContainers) {
		// Da es wahrscheinlich wenige Betreuungspensen innerhalb einer Betreuung gibt, macht es vielleicht mehr Sinn diese Version zu nutzen
		List<Betreuungspensum> gueltigkeitStream = betreuungspensumContainers.stream()
			.filter(cont -> "GS".equalsIgnoreCase(type) ? cont.getBetreuungspensumGS() != null : cont.getBetreuungspensumJA() != null)
			.map("GS".equalsIgnoreCase(type) ? BetreuungspensumContainer::getBetreuungspensumGS : BetreuungspensumContainer::getBetreuungspensumJA)
			.collect(Collectors.toList());

		//Achtung hier MUSS instanz verglichen werden
		return gueltigkeitStream.stream()
			.anyMatch(o1 -> gueltigkeitStream.stream()
				.anyMatch(o2 -> !o1.equals(o2) && o1.getGueltigkeit().intersects(o2.getGueltigkeit())));
	}
}
