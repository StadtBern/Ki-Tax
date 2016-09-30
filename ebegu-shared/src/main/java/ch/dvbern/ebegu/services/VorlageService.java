package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.Vorlage;

import javax.annotation.Nonnull;
import java.util.Optional;

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
