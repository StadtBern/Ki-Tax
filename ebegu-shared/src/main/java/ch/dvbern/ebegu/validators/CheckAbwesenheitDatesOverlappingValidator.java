package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.entities.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validator fuer Datum in Abwesenheiten. Die Zeitraeume duerfen sich nicht ueberschneiden
 */
public class CheckAbwesenheitDatesOverlappingValidator implements ConstraintValidator<CheckAbwesenheitDatesOverlapping, Betreuung> {

	@Override
	public void initialize(CheckAbwesenheitDatesOverlapping constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Betreuung instance, ConstraintValidatorContext context) {
		return !(checkOverlapping("JA", instance.getAbwesenheitContainers()) || checkOverlapping("GS", instance.getAbwesenheitContainers()));
	}

	/**
	 * prueft ob es eine ueberschneidung zwischen den Zeitrauemen gibt
	 */
	private boolean checkOverlapping(String type, Set<AbwesenheitContainer> abwesenheitContainers) {
		// Da es wahrscheinlich wenige Betreuungspensen innerhalb einer Betreuung gibt, macht es vielleicht mehr Sinn diese Version zu nutzen
		List<Abwesenheit> gueltigkeitStream = abwesenheitContainers.stream()
			.filter(cont -> "GS".equalsIgnoreCase(type) ? cont.getAbwesenheitGS() != null : cont.getAbwesenheitJA() != null)
			.map("GS".equalsIgnoreCase(type) ? AbwesenheitContainer::getAbwesenheitGS : AbwesenheitContainer::getAbwesenheitJA)
			.collect(Collectors.toList());

		//Achtung hier MUSS instanz verglichen werden
		return gueltigkeitStream.stream()
			.anyMatch(o1 -> gueltigkeitStream.stream()
				.anyMatch(o2 -> !o1.equals(o2) && o1.getGueltigkeit().intersects(o2.getGueltigkeit())));
	}

}
