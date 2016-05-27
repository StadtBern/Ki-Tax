package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungspensum;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.types.DateRange;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validator fuer Datum in Betreuungspensen
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

	private boolean checkOverlapping(String type, Set<BetreuungspensumContainer> betreuungspensumContainers) {
		// todo team Diese Version ist etwas schlecht weil O(n2). Mit einem normalen foreach waere es nur O(n), dafuer aber schwieriger zu verstehen
		// Da es wahrscheinlich wenige Betreuungspensen innerhalb einer Betreuung gibt, macht es vielleicht mehr Sinn diese Version zu nutzen
		Set<DateRange> gueltigkeitStream = betreuungspensumContainers.stream()
			.filter(cont -> type.equalsIgnoreCase("GS") ? cont.getBetreuungspensumGS() != null : cont.getBetreuungspensumJA() != null)
			.map(type.equalsIgnoreCase("GS") ? BetreuungspensumContainer::getBetreuungspensumGS : BetreuungspensumContainer::getBetreuungspensumJA)
			.map(Betreuungspensum::getGueltigkeit)
			.collect(Collectors.toSet());

		return gueltigkeitStream.stream()
			.anyMatch(o1 -> gueltigkeitStream.stream().anyMatch(o2 -> o1 != o2 && o1.intersects(o2)));
	}
}
