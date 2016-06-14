package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Institution_;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Institution
 */
@Stateless
@Local(InstitutionService.class)
public class InstitutionServiceBean extends AbstractBaseService implements InstitutionService {

	@Inject
	private Persistence<Institution> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Nonnull
	@Override
	public Institution updateInstitution(@Nonnull Institution institution) {
		Objects.requireNonNull(institution);
		return persistence.merge(institution);
	}

	@Nonnull
	@Override
	public Institution createInstitution(@Nonnull Institution institution) {
		Objects.requireNonNull(institution);
		return persistence.persist(institution);
	}

	@Nonnull
	@Override
	public Optional<Institution> findInstitution(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		Institution a =  persistence.find(Institution.class, id);
		return Optional.ofNullable(a);
	}

	@Override
	public void setInstitutionInactive(@Nonnull String institutionId) {
		Validate.notNull(institutionId);
		Optional<Institution> institutionToRemove = findInstitution(institutionId);

		Institution institution = institutionToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeInstitution", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionId));
		institution.setActive(false);
		persistence.merge(institution);
	}

	@Override
	public void deleteInstitution(@Nonnull String institutionId) {
		Validate.notNull(institutionId);
		Optional<Institution> institutionToRemove = findInstitution(institutionId);
		institutionToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeInstitution", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionId));
		persistence.remove(institutionToRemove.get());
	}

	@Override
	@Nonnull
	public Collection<Institution> getAllInstitutionenFromTraegerschaft(String traegerschaftId) {
		Traegerschaft traegerschaft = persistence.find(Traegerschaft.class, traegerschaftId);
		return criteriaQueryHelper.getEntitiesByAttribute(Institution.class, traegerschaft, Institution_.traegerschaft);
	}

	@Override
	@Nonnull
	public Collection<Institution> getAllActiveInstitutionen() {
		return criteriaQueryHelper.getEntitiesByAttribute(Institution.class, true, Institution_.active);
	}

	@Override
	public Collection<Institution> getAllInstitutionen() {
		return criteriaQueryHelper.getAll(Institution.class);
	}

}
