package ch.dvbern.ebegu.services;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;

/**
 * Service zum Verwalten von Gesuchstellern
 */
public interface GesuchstellerService {

	/**
	 * Aktualisiert die Gesuchsteller in der DB.
	 * @param gesuchsteller Die Gesuchsteller als DTO
	 * @param gsNumber Die Gesuchersteller-Nummer
	 */
	@Nonnull
	GesuchstellerContainer saveGesuchsteller(@Nonnull GesuchstellerContainer gesuchsteller, final Gesuch gesuch, Integer gsNumber, boolean umzug);

	/**

	 * @param id PK (id) der Gesuchsteller
	 * @return Gesuchsteller mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<GesuchstellerContainer> findGesuchsteller(@Nonnull String id);

	/**
	 *
	 * @return Liste aller Gesuchsteller aus der DB
	 */
	@Nonnull
	Collection<GesuchstellerContainer> getAllGesuchsteller();

	/**
	 * entfernt eine Gesuchsteller aus der Databse
	 * @param gesuchsteller Gesuchsteller zu entfernen
	 */
	void removeGesuchsteller(@Nonnull GesuchstellerContainer gesuchsteller);

	/**
	 * Sucht nach einem Gesuch an dem ein Gesuchsteller mit der uebergebenen ID angehaengt ist und gibt es zurueck
	 * Achtung hier wird keine authorisierung geprueft nicht direkt nach aussen zugaenglich machen
	 * @param gesuchstellerContainerID die Gesuchsteller ID deren Parentgesuch gefunden werden soll
	 * @return Das Gesuch an dem der Gesuchsteller angehaengt ist
	 */
	@Nullable
	Gesuch findGesuchOfGesuchsteller(@Nonnull  String gesuchstellerContainerID);
}
