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

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.ModulTagesschule;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer Modul
 */
@Stateless
@Local(ModulTagesschuleService.class)
public class ModulTagesschuleServiceBean extends AbstractBaseService implements ModulTagesschuleService {

	@Inject
	private Persistence persistence;

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	public ModulTagesschule saveModul(@Nonnull ModulTagesschule modulTagesschule) {
		Objects.requireNonNull(modulTagesschule);
		return persistence.merge(modulTagesschule);
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	public Optional<ModulTagesschule> findModul(@Nonnull String modulTagesschuleId) {
		Objects.requireNonNull(modulTagesschuleId, "id muss gesetzt sein");
		ModulTagesschule modul = persistence.find(ModulTagesschule.class, modulTagesschuleId);
		return Optional.ofNullable(modul);
	}

	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	public void removeModul(@Nonnull String modulTagesschuleId) {
		Validate.notNull(modulTagesschuleId);
		Optional<ModulTagesschule> modulOptional = findModul(modulTagesschuleId);
		ModulTagesschule modulToRemove = modulOptional.orElseThrow(() -> new EbeguEntityNotFoundException("removeModul", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND,
			modulTagesschuleId));
		persistence.remove(modulToRemove);
	}

}


