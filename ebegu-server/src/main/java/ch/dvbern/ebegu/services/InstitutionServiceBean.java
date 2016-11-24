package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Institution_;
import ch.dvbern.ebegu.entities.Traegerschaft_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer Institution
 */
@Stateless
@Local(InstitutionService.class)
@PermitAll
public class InstitutionServiceBean extends AbstractBaseService implements InstitutionService {

	@Inject
	private Persistence<Institution> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private BenutzerService benutzerService;

	@Nonnull
	@Override
	@RolesAllowed(value ={ADMIN, SUPER_ADMIN})
	public Institution updateInstitution(@Nonnull Institution institution) {
		Objects.requireNonNull(institution);
		return persistence.merge(institution);
	}

	@Nonnull
	@Override
	@RolesAllowed(value ={ADMIN, SUPER_ADMIN})
	public Institution createInstitution(@Nonnull Institution institution) {
		Objects.requireNonNull(institution);
		return persistence.persist(institution);
	}

	@Nonnull
	@Override
	@PermitAll
	public Optional<Institution> findInstitution(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		Institution a =  persistence.find(Institution.class, id);
		return Optional.ofNullable(a);
	}

	@Override
	@RolesAllowed(value ={ADMIN, SUPER_ADMIN})
	public void setInstitutionInactive(@Nonnull String institutionId) {
		Validate.notNull(institutionId);
		Optional<Institution> institutionToRemove = findInstitution(institutionId);

		Institution institution = institutionToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeInstitution", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionId));
		institution.setActive(false);
		persistence.merge(institution);
	}

	@Override
	@RolesAllowed(value ={ADMIN, SUPER_ADMIN})
	public void deleteInstitution(@Nonnull String institutionId) {
		Validate.notNull(institutionId);
		Optional<Institution> institutionToRemove = findInstitution(institutionId);
		institutionToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeInstitution", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionId));
		persistence.remove(institutionToRemove.get());
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<Institution> getAllInstitutionenFromTraegerschaft(String traegerschaftId) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Institution> query = cb.createQuery(Institution.class);
		Root<Institution> root = query.from(Institution.class);
		//Traegerschaft
		Predicate predTraegerschaft = cb.equal(root.get(Institution_.traegerschaft).get(Traegerschaft_.id), traegerschaftId);

		query.where(predTraegerschaft);
		return persistence.getCriteriaResults(query);
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<Institution> getAllActiveInstitutionenFromTraegerschaft(String traegerschaftId) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Institution> query = cb.createQuery(Institution.class);
		Root<Institution> root = query.from(Institution.class);
		//Traegerschaft
		Predicate predTraegerschaft = cb.equal(root.get(Institution_.traegerschaft).get(Traegerschaft_.id), traegerschaftId);
		Predicate predActive = cb.equal(root.get(Institution_.active), Boolean.TRUE);
		query.where(predTraegerschaft, predActive);
		return persistence.getCriteriaResults(query);
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<Institution> getAllActiveInstitutionen() {
		return criteriaQueryHelper.getEntitiesByAttribute(Institution.class, true, Institution_.active);
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<Institution> getAllInstitutionen() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Institution.class));
	}

	@Override
	@Nonnull
	@PermitAll
	public Collection<Institution> getInstitutionenForCurrentBenutzer() {
		Optional<Benutzer> benutzerOptional = benutzerService.getCurrentBenutzer();
		if (benutzerOptional.isPresent()) {
			Benutzer benutzer = benutzerOptional.get();
			if (UserRole.SACHBEARBEITER_TRAEGERSCHAFT.equals(benutzer.getRole()) && benutzer.getTraegerschaft() != null) {
				return getAllInstitutionenFromTraegerschaft(benutzer.getTraegerschaft().getId());
			}
			if (UserRole.SACHBEARBEITER_INSTITUTION.equals(benutzer.getRole()) && benutzer.getInstitution() != null) {
				List<Institution> institutionList = new ArrayList<>();
				if (benutzer.getInstitution() != null && benutzer.getInstitution().getActive()) {
					institutionList.add(benutzer.getInstitution());
				}
				return institutionList;
			}
		}
		return Collections.emptyList();
	}
}
