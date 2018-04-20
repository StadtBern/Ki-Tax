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

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungspensum;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;

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
