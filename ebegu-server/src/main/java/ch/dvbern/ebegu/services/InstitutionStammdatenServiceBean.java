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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
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
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.InstitutionStammdaten_;
import ch.dvbern.ebegu.entities.Institution_;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.ebegu.validationgroups.InstitutionsStammdatenInsertValidationGroup;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer InstitutionStammdaten
 */
@Stateless
@Local(InstitutionStammdatenService.class)
@PermitAll
public class InstitutionStammdatenServiceBean extends AbstractBaseService implements InstitutionStammdatenService {

	private static final String INSTITUTION_ID = "institutionId";
	private static final String GP_START = "gpStart";
	private static final String GP_END = "gpEnd";
	private static final String ANGEBOTSTYP = "angebotstyp";

	@Inject
	private BenutzerService benutzerService;

	@Inject
	private Persistence persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private InstitutionService institutionService;

	@Nonnull
	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	public InstitutionStammdaten saveInstitutionStammdaten(@Nonnull InstitutionStammdaten institutionStammdaten) {
		Objects.requireNonNull(institutionStammdaten);
		Validator validator = Validation.byDefaultProvider().configure().buildValidatorFactory().getValidator();
		Set<ConstraintViolation<InstitutionStammdaten>> violations = validator.validate(institutionStammdaten, InstitutionsStammdatenInsertValidationGroup.class);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		return persistence.merge(institutionStammdaten);
	}

	@Nonnull
	@Override
	@PermitAll
	public Optional<InstitutionStammdaten> findInstitutionStammdaten(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		InstitutionStammdaten a = persistence.find(InstitutionStammdaten.class, id);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<InstitutionStammdaten> getAllInstitutionStammdaten() {
		return new ArrayList<>(criteriaQueryHelper.getAll(InstitutionStammdaten.class));
	}

	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, ADMINISTRATOR_SCHULAMT })
	public void removeInstitutionStammdaten(@Nonnull String institutionStammdatenId) {
		Validate.notNull(institutionStammdatenId);
		Optional<InstitutionStammdaten> institutionStammdatenToRemove = findInstitutionStammdaten(institutionStammdatenId);
		final InstitutionStammdaten removeInstitutionStammdaten = institutionStammdatenToRemove.orElseThrow(() -> new EbeguEntityNotFoundException
			("removeInstitutionStammdaten", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionStammdatenId));
		persistence.remove(removeInstitutionStammdaten);
	}

	@Override
	@PermitAll
	public Collection<InstitutionStammdaten> getAllInstitutionStammdatenByDate(@Nonnull LocalDate date) {
		return new ArrayList<>(criteriaQueryHelper.getAllInInterval(InstitutionStammdaten.class, date));
	}

	@Override
	public Collection<InstitutionStammdaten> getAllActiveInstitutionStammdatenByGesuchsperiode(@Nonnull String gesuchsperiodeId) {

		Gesuchsperiode gesuchsperiode = persistence.find(Gesuchsperiode.class, gesuchsperiodeId);

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<InstitutionStammdaten> query = cb.createQuery(InstitutionStammdaten.class);
		Root<InstitutionStammdaten> root = query.from(InstitutionStammdaten.class);
		query.select(root);
		Join<InstitutionStammdaten, Institution> institution = root.join(InstitutionStammdaten_.institution, JoinType.INNER);
		Predicate isActivePredicate = cb.equal(institution.get(Institution_.active), Boolean.TRUE);

		ParameterExpression<LocalDate> startParam = cb.parameter(LocalDate.class, GP_START);
		ParameterExpression<LocalDate> endParam = cb.parameter(LocalDate.class, GP_END);

		// InstStammdaten Ende muss NACH GP Start sein
		// InstStammdaten Start muss VOR GP Ende sein
		Predicate startPredicate = cb.greaterThanOrEqualTo(root.get(InstitutionStammdaten_.gueltigkeit).get(DateRange_.gueltigBis), startParam);
		Predicate endPredicate = cb.lessThanOrEqualTo(root.get(InstitutionStammdaten_.gueltigkeit).get(DateRange_.gueltigAb), endParam);

		query.where(startPredicate, endPredicate, isActivePredicate);

		TypedQuery<InstitutionStammdaten> typedQuery = persistence.getEntityManager().createQuery(query);
		typedQuery.setParameter(GP_START, gesuchsperiode.getGueltigkeit().getGueltigAb());
		typedQuery.setParameter(GP_END, gesuchsperiode.getGueltigkeit().getGueltigBis());
		return typedQuery.getResultList();
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<InstitutionStammdaten> getAllInstitutionStammdatenByInstitution(String institutionId) {
		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		ParameterExpression<String> institutionIdParam = cb.parameter(String.class, INSTITUTION_ID);

		CriteriaQuery<InstitutionStammdaten> query = cb.createQuery(InstitutionStammdaten.class);
		Root<InstitutionStammdaten> root = query.from(InstitutionStammdaten.class);
		Predicate gesuchstellerPred = cb.equal(root.get(InstitutionStammdaten_.institution).get(Institution_.id), institutionIdParam);
		query.where(gesuchstellerPred);
		TypedQuery<InstitutionStammdaten> typedQuery = persistence.getEntityManager().createQuery(query);
		typedQuery.setParameter(INSTITUTION_ID, institutionId);
		return typedQuery.getResultList();
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<InstitutionStammdaten> getAllInstitutionStammdatenByInstitutionAndGesuchsperiode(@Nonnull String institutionId,
		@Nonnull BetreuungsangebotTyp betreuungsangebotTyp, @Nonnull Gesuchsperiode gesuchsperiode) {

		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		ParameterExpression<String> institutionIdParam = cb.parameter(String.class, INSTITUTION_ID);
		ParameterExpression<LocalDate> gueltigAbParam = cb.parameter(LocalDate.class, GP_START);
		ParameterExpression<LocalDate> gueltigBisParam = cb.parameter(LocalDate.class, GP_END);
		ParameterExpression<BetreuungsangebotTyp> angebotstypParam = cb.parameter(BetreuungsangebotTyp.class, ANGEBOTSTYP);

		CriteriaQuery<InstitutionStammdaten> query = cb.createQuery(InstitutionStammdaten.class);
		Root<InstitutionStammdaten> root = query.from(InstitutionStammdaten.class);
		Predicate institutionPredicate = cb.equal(root.get(InstitutionStammdaten_.institution).get(Institution_.id), institutionIdParam);
		Predicate startPredicate = cb.greaterThanOrEqualTo(root.get(InstitutionStammdaten_.gueltigkeit).get(DateRange_.gueltigBis), gueltigAbParam);
		Predicate endPredicate = cb.lessThanOrEqualTo(root.get(InstitutionStammdaten_.gueltigkeit).get(DateRange_.gueltigAb), gueltigBisParam);
		Predicate angebotstypPredicate = cb.equal(root.get(InstitutionStammdaten_.betreuungsangebotTyp), angebotstypParam);

		query.where(institutionPredicate, startPredicate, endPredicate, angebotstypPredicate);
		TypedQuery<InstitutionStammdaten> typedQuery = persistence.getEntityManager().createQuery(query);
		typedQuery.setParameter(INSTITUTION_ID, institutionId);
		typedQuery.setParameter(GP_START, gesuchsperiode.getGueltigkeit().getGueltigAb());
		typedQuery.setParameter(GP_END, gesuchsperiode.getGueltigkeit().getGueltigBis());
		typedQuery.setParameter(ANGEBOTSTYP, betreuungsangebotTyp);
		return typedQuery.getResultList();
	}

	@Override
	@PermitAll
	public Collection<BetreuungsangebotTyp> getBetreuungsangeboteForInstitutionenOfCurrentBenutzer() {
		Benutzer user = benutzerService.getCurrentBenutzer().orElseThrow(() -> new EbeguRuntimeException("getBetreuungsangeboteForInstitutionenOfCurrentBenutzer", "No User is logged in"));
		if (user.getRole().isRoleSchulamt()) { // fuer Schulamt muessen wir nichts machen. Direkt Schulamttypes zurueckgeben
			return BetreuungsangebotTyp.getSchulamtTypes();
		}
		Collection<Institution> institutionenForCurrentBenutzer = institutionService.getAllowedInstitutionenForCurrentBenutzer(false);
		if (institutionenForCurrentBenutzer.isEmpty()) {
			return new ArrayList<>();
		}

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<BetreuungsangebotTyp> query = cb.createQuery(BetreuungsangebotTyp.class);
		Root<InstitutionStammdaten> root = query.from(InstitutionStammdaten.class);
		query.select(root.get(InstitutionStammdaten_.betreuungsangebotTyp));
		query.distinct(true);

		ParameterExpression<LocalDate> dateParam = cb.parameter(LocalDate.class, "date");
		Predicate intervalPredicate = cb.between(dateParam,
			root.get(InstitutionStammdaten_.gueltigkeit).get(DateRange_.gueltigAb),
			root.get(InstitutionStammdaten_.gueltigkeit).get(DateRange_.gueltigBis));

		Predicate institutionPredicate = root.get(InstitutionStammdaten_.institution).in(institutionenForCurrentBenutzer);

		query.where(intervalPredicate, institutionPredicate);
		TypedQuery<BetreuungsangebotTyp> q = persistence.getEntityManager().createQuery(query).setParameter(dateParam, LocalDate.now());
		List<BetreuungsangebotTyp> resultList = q.getResultList();
		return resultList;
	}


}
