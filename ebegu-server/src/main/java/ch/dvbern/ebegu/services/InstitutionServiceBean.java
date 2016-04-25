package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
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

}
