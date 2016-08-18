package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Dokument;

import javax.annotation.Nonnull;
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
