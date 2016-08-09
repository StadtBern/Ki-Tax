package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Kind
 */
@Stateless
@Local(DokumentGrundService.class)
public class DokumentGrundServiceBean extends AbstractBaseService implements DokumentGrundService {

	@Inject
	private Persistence<DokumentGrund> persistence;

	@Nonnull
	@Override
	public DokumentGrund saveDokumentGrund(@Nonnull DokumentGrund dokumentGrund) {
		Objects.requireNonNull(dokumentGrund);
		return persistence.merge(dokumentGrund);
	}

	@Override
	@Nonnull
	public Optional<DokumentGrund> findDokumentGrund(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		DokumentGrund a = persistence.find(DokumentGrund.class, key);
		return Optional.ofNullable(a);
	}

}
