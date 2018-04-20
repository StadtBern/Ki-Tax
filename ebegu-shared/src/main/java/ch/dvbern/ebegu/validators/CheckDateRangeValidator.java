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

import javax.annotation.Nonnull;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ch.dvbern.ebegu.types.DateRange;

public class CheckDateRangeValidator implements ConstraintValidator<CheckDateRange, DateRange> {

	@Override
	public void initialize(CheckDateRange constraintAnnotation) {
		// nop
	}

	/**
	 * gueltigAb und gueltigBis duerfen auch gleich sein. Dies bedeutet eine Zeitspannung von 1 Tag
	 */
	@Override
	public boolean isValid(@Nonnull DateRange instance, ConstraintValidatorContext context) {
		return !instance.getGueltigAb().isAfter(instance.getGueltigBis());
	}
}
