/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.validators;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ch.dvbern.ebegu.entities.Abwesenheit;
import ch.dvbern.ebegu.entities.AbwesenheitContainer;
import ch.dvbern.ebegu.entities.Betreuung;

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
