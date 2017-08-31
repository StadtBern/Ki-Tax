package ch.dvbern.ebegu.services;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Vorlage;
import ch.dvbern.lib.cdipersistence.Persistence;

/**
 * Service fuer Vorlage
 */
@Stateless
@Local(VorlageService.class)
public class VorlageServiceBean extends AbstractBaseService implements VorlageService {

	@Inject
	private Persistence persistence;


	@Override
	@Nonnull
	public Optional<Vorlage> findVorlage(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Vorlage a = persistence.find(Vorlage.class, key);
		return Optional.ofNullable(a);
	}

}
