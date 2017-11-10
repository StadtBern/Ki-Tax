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

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.InstitutionStammdatenTagesschule;
import ch.dvbern.ebegu.entities.InstitutionStammdatenTagesschule_;
import ch.dvbern.ebegu.entities.InstitutionStammdaten_;
import ch.dvbern.ebegu.entities.ModulTagesschule;
import ch.dvbern.ebegu.entities.ModulTagesschule_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
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
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	public ModulTagesschule createModul(@Nonnull ModulTagesschule modulTagesschule) {
		Objects.requireNonNull(modulTagesschule);
		return persistence.persist(modulTagesschule);
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	public ModulTagesschule updateModul(@Nonnull ModulTagesschule modulTagesschule) {
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

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	//TODO wijo brauchts das getAll?
	public Collection<ModulTagesschule> getAllModule() {
		return new ArrayList<>(criteriaQueryHelper.getAll(ModulTagesschule.class));
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	//TODO wijo brauchts das findByName?
	public List<ModulTagesschule> findModulByName(String modulTagesschuleName) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<ModulTagesschule> query = cb.createQuery(ModulTagesschule.class);

		Root<ModulTagesschule> root = query.from(ModulTagesschule.class);

		ParameterExpression<String> nameParam = cb.parameter(String.class, "modulname");
		Predicate namePredicate = cb.equal(root.get(ModulTagesschule_.modulTagesschuleName), nameParam);

		query.where(namePredicate, namePredicate);
		TypedQuery<ModulTagesschule> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(nameParam, modulTagesschuleName);

		return q.getResultList();
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	public Collection<ModulTagesschule> findMondayModuleTagesschuleByInstitutionStammdaten(String institutionStammdatenID) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<ModulTagesschule> query = cb.createQuery(ModulTagesschule.class);
		ParameterExpression<String> institutionStammdatenIdParam = cb.parameter(String.class, "institutionStammdatenId");
		Root<InstitutionStammdaten> root = query.from(InstitutionStammdaten.class);
		Predicate institutionsStammdatenPredicate = cb.equal(root.get(InstitutionStammdaten_.id), institutionStammdatenIdParam);
		query.where(institutionsStammdatenPredicate);
		Join<InstitutionStammdaten, InstitutionStammdatenTagesschule> joinInstStammdatenTagesschule = root.join(InstitutionStammdaten_
			.institutionStammdatenTagesschule, JoinType.INNER);
		SetJoin<InstitutionStammdatenTagesschule, ModulTagesschule> joinTagesschulModule = joinInstStammdatenTagesschule.join(InstitutionStammdatenTagesschule_
			.moduleTagesschule, JoinType.INNER);
		Predicate mondayTagesschulModule = cb.equal(joinTagesschulModule.get(ModulTagesschule_.wochentag), DayOfWeek.MONDAY);
		query.select(joinTagesschulModule);
		query.where(institutionsStammdatenPredicate, mondayTagesschulModule);

		List<ModulTagesschule> modulTagesschuleList = persistence.getEntityManager().createQuery(query).setParameter("institutionStammdatenId",
			institutionStammdatenID).getResultList();
		return modulTagesschuleList;
	}

}


