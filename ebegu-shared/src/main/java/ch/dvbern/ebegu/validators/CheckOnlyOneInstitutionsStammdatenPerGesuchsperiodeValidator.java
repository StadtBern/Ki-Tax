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

import java.util.Collection;

import javax.enterprise.inject.spi.CDI;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;

/**
 * Pro Gesuchsperiode darf nur 1 InstitutionsStammdaten erfasst werden
 */
public class CheckOnlyOneInstitutionsStammdatenPerGesuchsperiodeValidator
	implements ConstraintValidator<CheckOnlyOneInstitutionsStammdatenPerGesuchsperiode, InstitutionStammdaten> {

	private GesuchsperiodeService gesuchsperiodeService;
	private InstitutionStammdatenService institutionStammdatenService;


	public CheckOnlyOneInstitutionsStammdatenPerGesuchsperiodeValidator() {

	}

	@Override
	public void initialize(CheckOnlyOneInstitutionsStammdatenPerGesuchsperiode constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(InstitutionStammdaten stammdaten, ConstraintValidatorContext context) {
		// Gesuchsperiode dieser Stammdaten ermitteln
		Collection<Gesuchsperiode> gesuchsperiodeList = getGesuchsperiodeService().getGesuchsperiodenBetween(
			stammdaten.getGueltigkeit().getGueltigAb(), stammdaten.getGueltigkeit().getGueltigBis());

		for (Gesuchsperiode gesuchsperiode : gesuchsperiodeList) {
			if (!isValidForGesuchsperiode(stammdaten, gesuchsperiode)) {
				return false;
			}
		}
		return true;
	}

	private boolean isValidForGesuchsperiode(InstitutionStammdaten stammdaten, Gesuchsperiode gesuchsperiode) {
		Collection<InstitutionStammdaten> stammdatenList = getInstitutionStammdatenService().getAllInstitutionStammdatenByInstitutionAndGesuchsperiode(
			stammdaten.getInstitution().getId(), stammdaten.getBetreuungsangebotTyp(), gesuchsperiode);

		if (stammdatenList.size() > 1) {
			// Sowieso schon falsch
			return false;
		} else if (stammdatenList.size() == 1) {
			// Es muss dieses sein
			return stammdatenList.iterator().next().equals(stammdaten);
		}
		return true;
	}

	private GesuchsperiodeService getGesuchsperiodeService() {
		if (gesuchsperiodeService == null) {
			//FIXME: das ist nur ein Ugly Workaround, weil CDI-Injection in Wildfly 10 nicht funktioniert.
			//noinspection NonThreadSafeLazyInitialization
			gesuchsperiodeService = CDI.current().select(GesuchsperiodeService.class).get();
		}
		return gesuchsperiodeService;
	}

	private InstitutionStammdatenService getInstitutionStammdatenService() {
		if (institutionStammdatenService == null) {
			//FIXME: das ist nur ein Ugly Workaround, weil CDI-Injection in Wildfly 10 nicht funktioniert.
			//noinspection NonThreadSafeLazyInitialization
			institutionStammdatenService = CDI.current().select(InstitutionStammdatenService.class).get();
		}
		return institutionStammdatenService;
	}
}
