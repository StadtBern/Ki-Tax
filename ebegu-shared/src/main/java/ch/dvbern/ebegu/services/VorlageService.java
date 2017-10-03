package ch.dvbern.ebegu.services;

import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Vorlage;

/**
 * Service zum Verwalten von Vorlagen
 */
public interface VorlageService {

	/**
	 * @param key PK (id) des Dokument
	 * @return Vorlage mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Vorlage> findVorlage(@Nonnull String key);

}
