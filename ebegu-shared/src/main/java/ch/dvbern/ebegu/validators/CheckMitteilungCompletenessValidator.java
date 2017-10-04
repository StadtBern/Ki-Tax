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

import ch.dvbern.ebegu.entities.Mitteilung;

/**
 * Validator fuer Mitteilungen
 */
public class CheckMitteilungCompletenessValidator implements ConstraintValidator<CheckMitteilungCompleteness, Mitteilung> {

	@Override
	public void initialize(CheckMitteilungCompleteness constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Mitteilung instance, ConstraintValidatorContext context) {
		return instance.isEntwurf()
			|| (instance.getMessage() != null && !instance.getMessage().isEmpty()
			&& instance.getSubject() != null && !instance.getSubject().isEmpty());
	}
}
