package ch.dvbern.ebegu.services;

import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Dokument;

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
