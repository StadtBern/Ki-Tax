package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer InstitutionStammdaten
 */
@Stateless
@Local(InstitutionStammdatenService.class)
public class InstitutionStammdatenServiceBean extends AbstractBaseService implements InstitutionStammdatenService {

	@Inject
	private Persistence<InstitutionStammdaten> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
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
}
