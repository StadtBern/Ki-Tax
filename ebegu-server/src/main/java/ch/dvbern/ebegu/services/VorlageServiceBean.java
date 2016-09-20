package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Vorlage;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Vorlage
 */
@Stateless
@Local(VorlageService.class)
public class VorlageServiceBean extends AbstractBaseService implements VorlageService {

	@Inject
	private Persistence<Vorlage> persistence;


	@Override
	@Nonnull
	public Optional<Vorlage> findVorlage(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Vorlage a = persistence.find(Vorlage.class, key);
		return Optional.ofNullable(a);
	}

}
