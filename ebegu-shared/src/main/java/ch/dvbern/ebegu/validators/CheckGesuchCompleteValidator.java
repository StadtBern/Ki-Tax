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

import ch.dvbern.ebegu.entities.Gesuch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dieser Validator die Komplettheit und Gültigkeit eines Gesuchs
 */
@SuppressWarnings({ "ConstantConditions", "PMD.CollapsibleIfStatements" })
public class CheckGesuchCompleteValidator implements ConstraintValidator<CheckGesuchComplete, Gesuch> {

	private static final Logger LOG = LoggerFactory.getLogger(CheckGesuchCompleteValidator.class.getSimpleName());

	@Override
	public void initialize(CheckGesuchComplete constraintAnnotation) {
		//nop
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean isValid(Gesuch gesuch, ConstraintValidatorContext context) {
		boolean valid = true;
		// Familiensituation
		if (gesuch.getFamiliensituationContainer() == null) {
			LOG.error("FamiliensituationContainer is empty for Gesuch {}", gesuch.getId());
			valid = false;
		}
		// Gesuchsteller 1
		if (gesuch.getGesuchsteller1() == null) {
			LOG.error("FamiliensituationContainer is empty for Gesuch {}", gesuch.getId());
			valid = false;
		}
		// Gesuchsteller 2
		if (gesuch.getFamiliensituationContainer().getFamiliensituationJA().hasSecondGesuchsteller()) {
			if (gesuch.getGesuchsteller2() == null) {
				LOG.error("FamiliensituationContainer is empty for Gesuch {}", gesuch.getId());
				valid = false;
			}
		}
		return valid;
	}
}
