package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.GeneratedDokument;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Objects;

/**
 * Service fuer GeneratedDokument
 */
@Stateless
@Local(GeneratedDokumentService.class)
public class GeneratedDokumentServiceBean extends AbstractBaseService implements GeneratedDokumentService {

	@Inject
	private Persistence<GeneratedDokument> persistence;

	@Override
	@Nonnull
	public GeneratedDokument saveGeneratedDokument(@Nonnull GeneratedDokument dokument) {
		Objects.requireNonNull(dokument);
		return persistence.merge(dokument);
	}

}
