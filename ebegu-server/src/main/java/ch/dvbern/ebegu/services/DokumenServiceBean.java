package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.DokumentGrund_;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Dokument
 */
@Stateless
@Local(DokumentService.class)
public class DokumenServiceBean extends AbstractBaseService implements DokumentService {

	@Inject
	private Persistence<Dokument> persistence;


	@Override
	@Nonnull
	public Optional<Dokument> findDokument(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Dokument a = persistence.find(Dokument.class, key);
		return Optional.ofNullable(a);
	}

}
