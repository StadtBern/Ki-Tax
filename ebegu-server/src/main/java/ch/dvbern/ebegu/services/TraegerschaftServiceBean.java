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
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.BerechtigungHistory;
import ch.dvbern.ebegu.entities.BerechtigungHistory_;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.entities.Traegerschaft_;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.REVISOR;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer Traegerschaft
 */
@Stateless
@Local(TraegerschaftService.class)
public class TraegerschaftServiceBean extends AbstractBaseService implements TraegerschaftService {

	@Inject
	private Persistence persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private InstitutionService institutionService;


	@Nonnull
	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN })
	public Traegerschaft saveTraegerschaft(@Nonnull Traegerschaft traegerschaft) {
		Objects.requireNonNull(traegerschaft);
		return persistence.merge(traegerschaft);
	}

	@Nonnull
	@Override
	@PermitAll
	public Optional<Traegerschaft> findTraegerschaft(@Nonnull final String traegerschaftId) {
		Objects.requireNonNull(traegerschaftId, "id muss gesetzt sein");
		Traegerschaft a = persistence.find(Traegerschaft.class, traegerschaftId);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<Traegerschaft> getAllActiveTraegerschaften() {
		return criteriaQueryHelper.getEntitiesByAttribute(Traegerschaft.class, true, Traegerschaft_.active);
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<Traegerschaft> getAllTraegerschaften() {
		return new ArrayList<>(criteriaQueryHelper.getAllOrdered(Traegerschaft.class, Traegerschaft_.name));
	}

	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN })
	public void removeTraegerschaft(@Nonnull String traegerschaftId) {
		Validate.notNull(traegerschaftId);
		Optional<Traegerschaft> traegerschaftToRemove = findTraegerschaft(traegerschaftId);
		Traegerschaft traegerschaft = traegerschaftToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeTraegerschaft",
			ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, traegerschaftId));

		// Es müssen auch alle Berechtigungen für diese Traegerschaft gelöscht werden
		Collection<BerechtigungHistory> berechtigungenToDelete = criteriaQueryHelper.getEntitiesByAttribute(BerechtigungHistory.class, traegerschaft,
			BerechtigungHistory_.traegerschaft);
		for (BerechtigungHistory berechtigungHistory : berechtigungenToDelete) {
			persistence.remove(berechtigungHistory);
		}

		persistence.remove(traegerschaft);
	}

	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN })
	public void setInactive(@Nonnull String traegerschaftId) {
		Validate.notNull(traegerschaftId);
		Optional<Traegerschaft> traegerschaftOptional = findTraegerschaft(traegerschaftId);
		Traegerschaft traegerschaft = traegerschaftOptional.orElseThrow(() -> new EbeguEntityNotFoundException("setInactive", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, traegerschaftId));
		traegerschaft.setActive(false);
		persistence.merge(traegerschaft);
	}

	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, ADMINISTRATOR_SCHULAMT, REVISOR })
	public EnumSet<BetreuungsangebotTyp> getAllAngeboteFromTraegerschaft(@Nonnull String traegerschaftId) {
		Validate.notNull(traegerschaftId);
		Optional<Traegerschaft> traegerschaftOptional = findTraegerschaft(traegerschaftId);
		Traegerschaft traegerschaft = traegerschaftOptional.orElseThrow(() -> new EbeguEntityNotFoundException("setInactive", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, traegerschaftId));

		EnumSet<BetreuungsangebotTyp> result = EnumSet.noneOf(BetreuungsangebotTyp.class);

		Collection<Institution> allInstitutionen = institutionService.getAllInstitutionenFromTraegerschaft(traegerschaft.getId());
		allInstitutionen.forEach(institution -> {
			EnumSet<BetreuungsangebotTyp> allAngeboteInstitution = institutionService.getAllAngeboteFromInstitution(institution.getId());
			result.addAll(allAngeboteInstitution);
		});

		return result;
	}
}
