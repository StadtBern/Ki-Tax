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

package ch.dvbern.ebegu.services;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.GesuchDeletionCause;
import ch.dvbern.ebegu.enums.UserRoleName;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.GESUCHSTELLER;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Interface um gewisse Services als SUPER_ADMIN aufrufen zu koennen
 */
@Stateless
@Local(SuperAdminService.class)
@RunAs(value = UserRoleName.SUPER_ADMIN)
public class SuperAdminServiceBean implements SuperAdminService {

	@Inject
	private GesuchService gesuchService;

	@Inject
	private FallService fallService;

	@Override
	@RolesAllowed({ GESUCHSTELLER, SUPER_ADMIN, ADMIN, ADMINISTRATOR_SCHULAMT })
	public void removeGesuch(@Nonnull String gesuchId) {
		gesuchService.removeGesuch(gesuchId, GesuchDeletionCause.USER);
	}

	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN })
	public void removeFall(@Nonnull Fall fall) {
		fallService.removeFall(fall);
	}

	@Override
	@Nonnull
	@RolesAllowed(value = { SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA })
	public Gesuch updateGesuch(@Nonnull Gesuch gesuch, boolean saveInStatusHistory, Benutzer saveAsUser) {
		return gesuchService.updateGesuch(gesuch, saveInStatusHistory, saveAsUser);
	}
}
