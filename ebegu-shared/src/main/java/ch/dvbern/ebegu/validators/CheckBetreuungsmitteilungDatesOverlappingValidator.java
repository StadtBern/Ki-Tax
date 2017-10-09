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

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ch.dvbern.ebegu.entities.Betreuungsmitteilung;
import ch.dvbern.ebegu.entities.BetreuungsmitteilungPensum;

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
	 */
	private boolean checkOverlapping(Set<BetreuungsmitteilungPensum> betMitteilungPensum) {
		return betMitteilungPensum.stream()
			.anyMatch(o1 -> betMitteilungPensum.stream()
				.anyMatch(o2 -> !o1.equals(o2) && o1.getGueltigkeit().intersects(o2.getGueltigkeit())));
	}
}
