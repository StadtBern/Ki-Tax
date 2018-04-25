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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ch.dvbern.ebegu.entities.Berechtigung;
import ch.dvbern.ebegu.enums.UserRole;

/**
 * Dieser Validator prueft dass die angelegte Benutzer, mit den richtigen Parameter erstellt werden.
 */
public class CheckBerechtigungValidator implements ConstraintValidator<CheckBerechtigung, Berechtigung> {

	@Override
	public void initialize(CheckBerechtigung constraintAnnotation) {
		//nop
	}

	/**
	 * Folgende Regeln muessen erfuellt werden:
	 * - Wenn Rolle=SACHBEARBEITER_INSTITUTION der Benutzer muss mit einer Institution verknuepft werden
	 * - Wenn Rolle=SACHBEARBEITER_TRAEGERSCHAFT der Benutzer muss mit einer Traegerschaft verknuepft werden
	 *
	 * @param instance Benutzer
	 * @param context context
	 * @return true wenn die Regeln erfuellt sind
	 */
	@Override
	public boolean isValid(Berechtigung instance, ConstraintValidatorContext context) {
		if (UserRole.SACHBEARBEITER_INSTITUTION == instance.getRole()) {
			return instance.getInstitution() != null;
		}
		if (UserRole.SACHBEARBEITER_TRAEGERSCHAFT == instance.getRole()) {
			return instance.getTraegerschaft() != null;
		}

		return instance.getTraegerschaft() == null && instance.getInstitution() == null; // alle anderen Benutzer duerfen keine Inst/Traeg haben
	}
}
