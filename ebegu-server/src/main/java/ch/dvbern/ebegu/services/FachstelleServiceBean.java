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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Fachstelle;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer fachstelle
 */
@Stateless
@Local(FachstelleService.class)
@PermitAll
public class FachstelleServiceBean extends AbstractBaseService implements FachstelleService {

	@Inject
	private Persistence persistence;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Nonnull
	@Override
	@RolesAllowed(value = { ADMIN, SUPER_ADMIN })
	public Fachstelle saveFachstelle(@Nonnull Fachstelle fachstelle) {
		Objects.requireNonNull(fachstelle);
		return persistence.merge(fachstelle);
	}

	@Nonnull
	@Override
	public Optional<Fachstelle> findFachstelle(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Fachstelle a = persistence.find(Fachstelle.class, key);
		return Optional.ofNullable(a);
	}

	@Nonnull
	@Override
	public Collection<Fachstelle> getAllFachstellen() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Fachstelle.class));
	}

	@Override
	@RolesAllowed(value = { ADMIN, SUPER_ADMIN })
	public void removeFachstelle(@Nonnull String fachstelleId) {
		Objects.requireNonNull(fachstelleId);
		Optional<Fachstelle> fachstelleToRemove = findFachstelle(fachstelleId);
		fachstelleToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeFachstelle", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, fachstelleId));
		persistence.remove(fachstelleToRemove.get());
	}

}
