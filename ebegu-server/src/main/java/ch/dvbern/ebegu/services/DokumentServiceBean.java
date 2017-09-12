package ch.dvbern.ebegu.services;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.lib.cdipersistence.Persistence;

/**
 * Service fuer Dokument
 */
@Stateless
@Local(DokumentService.class)
public class DokumentServiceBean extends AbstractBaseService implements DokumentService {

	@Inject
	private Persistence persistence;


	@Override
	@Nonnull
	public Optional<Dokument> findDokument(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Dokument a = persistence.find(Dokument.class, key);
		return Optional.ofNullable(a);
	}

}
