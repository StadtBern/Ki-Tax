package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Dokumenten
 */
public interface DokumentService {

	/**
	 * @param key PK (id) des Dokument
	 * @return Dokument mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Dokument> findDokument(@Nonnull String key);

}
