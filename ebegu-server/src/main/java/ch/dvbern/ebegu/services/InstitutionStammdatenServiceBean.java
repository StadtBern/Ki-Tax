package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.InstitutionStammdaten_;
import ch.dvbern.ebegu.entities.Institution_;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Service fuer InstitutionStammdaten
 */
@Stateless
@Local(InstitutionStammdatenService.class)
@PermitAll
public class InstitutionStammdatenServiceBean extends AbstractBaseService implements InstitutionStammdatenService {

	@Inject
	private Persistence<InstitutionStammdaten> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private InstitutionService institutionService;


	@Nonnull
	@Override
	@RolesAllowed(value ={UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN})
	public InstitutionStammdaten saveInstitutionStammdaten(@Nonnull InstitutionStammdaten institutionStammdaten) {
		Objects.requireNonNull(institutionStammdaten);
		return persistence.merge(institutionStammdaten);
	}

	@Nonnull
	@Override
	public Optional<InstitutionStammdaten> findInstitutionStammdaten(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		InstitutionStammdaten a =  persistence.find(InstitutionStammdaten.class, id);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public Collection<InstitutionStammdaten> getAllInstitutionStammdaten() {
		return new ArrayList<>(criteriaQueryHelper.getAll(InstitutionStammdaten.class));
	}

	@Override
	@RolesAllowed(value ={UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN})
	public void removeInstitutionStammdaten(@Nonnull String institutionStammdatenId) {
		Validate.notNull(institutionStammdatenId);
		Optional<InstitutionStammdaten> institutionStammdatenToRemove = findInstitutionStammdaten(institutionStammdatenId);
		institutionStammdatenToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeInstitutionStammdaten", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionStammdatenId));
		persistence.remove(institutionStammdatenToRemove.get());
	}

	@Override
	public Collection<InstitutionStammdaten> getAllInstitutionStammdatenByDate(@Nonnull LocalDate date) {
		return new ArrayList<>(criteriaQueryHelper.getAllInInterval(InstitutionStammdaten.class, date));
	}

	@Override
	@Nonnull
	public Collection<InstitutionStammdaten> getAllInstitutionStammdatenByInstitution(String institutionId) {
		List<InstitutionStammdaten> resultList = getQueryAllInstitutionStammdatenByInstitution(institutionId).getResultList();
		return resultList;
	}

	@Override
	public Collection<BetreuungsangebotTyp> getBetreuungsangeboteForInstitutionenOfCurrentBenutzer() {
		Collection<Institution> institutionenForCurrentBenutzer = institutionService.getInstitutionenForCurrentBenutzer();
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

	private TypedQuery<InstitutionStammdaten> getQueryAllInstitutionStammdatenByInstitution(@Nonnull String institutionId) {
		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		ParameterExpression<String> institutionIdParam = cb.parameter(String.class, "institutionId");

		CriteriaQuery<InstitutionStammdaten> query = cb.createQuery(InstitutionStammdaten.class);
		Root<InstitutionStammdaten> root = query.from(InstitutionStammdaten.class);
		Predicate gesuchstellerPred = cb.equal(root.get(InstitutionStammdaten_.institution).get(Institution_.id),institutionIdParam);
		query.where(gesuchstellerPred);
		TypedQuery<InstitutionStammdaten> typedQuery = persistence.getEntityManager().createQuery(query);
		typedQuery.setParameter("institutionId", institutionId);
		return typedQuery;
	}
}
