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

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;

/**
 * Die Betreuungspensen einer Betreuung müssen innerhalb der Verfügbarkeit der Institution liegen (Zeitraum der Institutionsstammdaten)
 */
public class CheckBetreuungZeitraumInstitutionsStammdatenZeitraumValidator implements ConstraintValidator<CheckBetreuungZeitraumInstitutionsStammdatenZeitraum, Betreuung> {

	@Override
	public void initialize(CheckBetreuungZeitraumInstitutionsStammdatenZeitraum constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Betreuung betreuung, ConstraintValidatorContext context) {
		DateRange institutionStammdatenDateRange = betreuung.getInstitutionStammdaten().getGueltigkeit();
		for (BetreuungspensumContainer betreuungspensumContainer : betreuung.getBetreuungspensumContainers()) {
			DateRange pensumDateRange = betreuungspensumContainer.getBetreuungspensumJA().getGueltigkeit();
			// Wenn Stammdaten <= GP.Ende -> muss contains
			// sonst -> nur VON datum pruefen

			// Falls die Institution bis mindestens ende GP existiert, dann interessiert uns das Ende der Betreuung nicht, da es sowieso innerhalb des
			// Institutions-Zeitraums liegt (da uns nur die GP interessiert)
			// todo dies muss noch verbessert werden -> institutionDateRange und pensumDateRange auf gesuchsperiode reduzieren und erst dann vergleichen
//			boolean institutionIstBisEndeGP = !institutionStammdatenDateRange.getGueltigBis().isBefore(betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis());
//			if (institutionIstBisEndeGP) {
//				// Wir muessen nur das VON Datum pruefen
//				if (!institutionStammdatenDateRange.contains(pensumDateRange.getGueltigAb())) {
//					return false;
//				}
//			} else if (!institutionStammdatenDateRange.contains(pensumDateRange)) {
//					return false;
//			}


			if (!institutionStammdatenDateRange.contains(pensumDateRange.getGueltigAb())
				|| (!institutionStammdatenDateRange.contains(pensumDateRange.getGueltigBis()) && !pensumDateRange.getGueltigBis().isEqual(Constants.END_OF_TIME))) {
				setConstraintViolationMessage(institutionStammdatenDateRange, context);
				return false;
			}
		}
		return true;
	}



	private void setConstraintViolationMessage(@NotNull DateRange institutionStammdatenDateRange, @NotNull ConstraintValidatorContext context) {
		ResourceBundle rb = ResourceBundle.getBundle("ValidationMessages");
		String message = rb.getString("invalid_betreuungszeitraum_for_institutionsstammdaten");
		message = MessageFormat.format(message, Constants.DATE_FORMATTER.format(institutionStammdatenDateRange.getGueltigAb()),
			Constants.DATE_FORMATTER.format(institutionStammdatenDateRange.getGueltigBis()));

		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message)
			.addConstraintViolation();
	}
}
