package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Kind;

import javax.annotation.Nonnull;
import java.util.Collection;
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
	Kind saveKind(@Nonnull Kind kind);

	/**

	 * @param key PK (id) des Kindes
	 * @return Kind mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Kind> findKind(@Nonnull String key);

	/**
	 * entfernt ein Kind aus der Databse
	 * @param kindId Id des Kindes zu entfernen
	 */
	void removeKind(@Nonnull String kindId);

	/**
	 *
	 * @param gesuchId Gesuch ID
	 * @return Liste aller Kinder vom gegebenen Gesuch aus der DB
     */
	@Nonnull
	Collection<Kind> getAllKinderFromGesuch(@Nonnull String gesuchId);

}
