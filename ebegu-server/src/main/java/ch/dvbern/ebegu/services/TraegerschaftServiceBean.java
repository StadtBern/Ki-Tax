package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Traegerschaft
 */
@Stateless
@Local(TraegerschaftService.class)
public class TraegerschaftServiceBean extends AbstractBaseService implements TraegerschaftService {

	@Inject
	private Persistence<Traegerschaft> persistence;


	@Nonnull
	@Override
	public Traegerschaft createTraegerschaft(@Nonnull Traegerschaft traegerschaft) {
		Objects.requireNonNull(traegerschaft);
		return persistence.persist(traegerschaft);
	}

	@Nonnull
	@Override
	public Optional<Traegerschaft> findTraegerschaft(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		Traegerschaft a =  persistence.find(Traegerschaft.class, id);
		return Optional.ofNullable(a);
	}

}
