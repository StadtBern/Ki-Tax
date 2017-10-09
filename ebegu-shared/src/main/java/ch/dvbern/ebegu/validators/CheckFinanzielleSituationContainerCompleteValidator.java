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

import ch.dvbern.ebegu.entities.FinanzielleSituationContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dieser Validator die Komplettheit und Gültigkeit eines FinanzielleSituationContainer
 */
public class CheckFinanzielleSituationContainerCompleteValidator implements
	ConstraintValidator<CheckFinanzielleSituationContainerComplete, FinanzielleSituationContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(CheckFinanzielleSituationContainerCompleteValidator.class.getSimpleName());

	@Override
	public void initialize(CheckFinanzielleSituationContainerComplete constraintAnnotation) {
		//nop
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean isValid(FinanzielleSituationContainer finSitContainer, ConstraintValidatorContext context) {
		boolean valid = true;
		if (finSitContainer.getFinanzielleSituationJA() == null) {
			LOG.error("FinanzielleSituationJA is empty for FinanzielleSituationContainer {}", finSitContainer.getId());
			valid = false;
		}
		if (finSitContainer.getGesuchsteller() == null) {
			LOG.error("GesuchstellerContainer is empty for FinanzielleSituationContainer {}", finSitContainer.getId());
			valid = false;
		}
		return valid;
	}
}
