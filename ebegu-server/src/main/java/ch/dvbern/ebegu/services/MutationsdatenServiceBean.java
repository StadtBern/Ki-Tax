package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Mutationsdaten;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Gesuch
 */
@Stateless
@Local(MutationsdatenService.class)
public class MutationsdatenServiceBean extends AbstractBaseService implements MutationsdatenService {

	@Inject
	private Persistence<Mutationsdaten> persistence;

	@Override
	@Nonnull
	public Optional<Mutationsdaten> findMutationsdaten(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Mutationsdaten a = persistence.find(Mutationsdaten.class, key);
		return Optional.ofNullable(a);
	}
}
