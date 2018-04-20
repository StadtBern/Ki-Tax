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

import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dieser Validator die Komplettheit und Gültigkeit eines GesuchstellerContainers
 */
public class CheckGesuchstellerContainerCompleteValidator implements
	ConstraintValidator<CheckGesuchstellerContainerComplete, GesuchstellerContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(CheckGesuchstellerContainerCompleteValidator.class.getSimpleName());

	@Override
	public void initialize(CheckGesuchstellerContainerComplete constraintAnnotation) {
		//nop
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean isValid(GesuchstellerContainer gsContainer, ConstraintValidatorContext context) {
		boolean valid = true;
		if (gsContainer.getGesuchstellerJA() == null) {
			LOG.error("GesuchstellerJA is empty for GesuchstellerContainer {}", gsContainer.getId());
			valid = false;
		}
		// The check if the finanzielleSitutionContaier is empty can not be done here any more since it can now be empty for a given set of Angebote
		// it should be checked that FinSit is not empty according to the values of sozialhilfe and verguenstigungGewuenscht
		// but this cannot be checked here since we have no access to the Gesuch
		return valid;
	}
}
