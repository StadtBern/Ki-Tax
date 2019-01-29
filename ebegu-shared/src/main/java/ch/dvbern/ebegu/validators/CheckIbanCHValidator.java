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

import java.util.Locale;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ch.dvbern.ebegu.entities.Gesuchsteller;
import com.google.common.base.Strings;

/**
 * If an IBAN is set then it must be swiss. In a valid IBAN number the country code is defined by the 2 first digits.
 * check http://www.swissiban.com/
 */
public class CheckIbanCHValidator implements ConstraintValidator<CheckIbanCH, Gesuchsteller> {

	@Override
	public void initialize(CheckIbanCH constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Gesuchsteller instance, ConstraintValidatorContext context) {
		if (instance.getIban() != null && !Strings.isNullOrEmpty(instance.getIban().getIban())) {
			final String countryCode = instance.getIban().getIban().substring(0, 2).toUpperCase(Locale.ENGLISH);
			return "CH".equals(countryCode);
		}
		return true;
	}
}
