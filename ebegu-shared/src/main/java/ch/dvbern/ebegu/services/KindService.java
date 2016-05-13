package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.KindContainer;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Service zum Verwalten von Kindern
 */
public interface KindService {

	/**
	 * Speichert das Kind neu in der DB falls der Key noch nicht existiert. Sonst wird das existierende Kind aktualisiert
	 * @param kind Das Kind als DTO
	 */
	@Nonnull
	KindContainer saveKind(@Nonnull KindContainer kind);

	/**
	 * @param key PK (id) des Kindes
	 * @return Kind mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<KindContainer> findKind(@Nonnull String key);

	/**
	 * entfernt ein Kind aus der Databse
	 * @param kindId Id des Kindes zu entfernen
	 */
	void removeKind(@Nonnull String kindId);

}
