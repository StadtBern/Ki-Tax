package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Mandanten
 */
@Stateless
@Local(MandantService.class)
public class MandantServiceBean extends AbstractBaseService implements MandantService {

	@Inject
	private Persistence<Mandant> persistence;

	@Nonnull
	@Override
	public Optional<Mandant> findMandant(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		Mandant a =  persistence.find(Mandant.class, id);
		return Optional.ofNullable(a);
	}

}
