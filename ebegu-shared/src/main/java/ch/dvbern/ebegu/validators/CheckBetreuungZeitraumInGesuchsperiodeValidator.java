/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

import java.util.ResourceBundle;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.types.DateRange;

/**
 * Die Betreuungspensen einer Betreuung duerfen nicht komplett ausserhalb der Gesuchsperiode liegen
 */
public class CheckBetreuungZeitraumInGesuchsperiodeValidator implements ConstraintValidator<CheckBetreuungZeitraumInGesuchsperiode, Betreuung> {

	@Override
	public void initialize(CheckBetreuungZeitraumInGesuchsperiode constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Betreuung betreuung, ConstraintValidatorContext context) {
		for (BetreuungspensumContainer betreuungspensumContainer : betreuung.getBetreuungspensumContainers()) {
			final DateRange pensumDateRange = betreuungspensumContainer.getBetreuungspensumJA().getGueltigkeit();
			final DateRange gueltigkeitGesuchsperiode = betreuung.extractGesuchsperiode().getGueltigkeit();

			if (!gueltigkeitGesuchsperiode.getOverlap(pensumDateRange).isPresent()) {
				setConstraintViolationMessage(context);
				return false;
			}
		}
		return true;
	}

	private void setConstraintViolationMessage(@NotNull ConstraintValidatorContext context) {
		ResourceBundle rb = ResourceBundle.getBundle("ValidationMessages");
		String message = rb.getString("invalid_betreuungszeitraum_for_gesuchsperiode");
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message)
			.addConstraintViolation();
	}
}
