package ch.dvbern.ebegu.services;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.validation.Valid;

import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;

/**
 * Service zum Verwalten von Erwerbspensen
 */
public interface ErwerbspensumService {

	/**
	 * Speichert die Erwerbspensen neu in der DB falls der Key noch nicht existiert.
	 *
	 * @param erwerbspensumContainer Das Erwerbspensum das gespeichert werden soll
	 */
	@Nonnull
	ErwerbspensumContainer saveErwerbspensum(@Valid @Nonnull ErwerbspensumContainer erwerbspensumContainer, Gesuch gesuch);

	/**
	 * @param key PK (id) des ErwerbspensumContainers
	 * @return Optional mit dem  ErwerbspensumContainers mit fuer den gegebenen Key
	 */
	@Nonnull
	Optional<ErwerbspensumContainer> findErwerbspensum(@Nonnull String key);

	/**
	 * Sucht die Erwerbspensen des übergebenen Gesuchstellers.
	 */
	Collection<ErwerbspensumContainer> findErwerbspensenForGesuchsteller(@Nonnull GesuchstellerContainer gesuchsteller);

	/**
	 * Sucht alle Erwerbspensen fuer das eingegebene Gesuch
	 */
	Collection<ErwerbspensumContainer> findErwerbspensenFromGesuch(@Nonnull String gesuchId);

	/**
	 * @return Liste aller ErwerbspensumContainer aus der DB
	 */
	@Nonnull
	Collection<ErwerbspensumContainer> getAllErwerbspensenenContainer();

	/**
	 * entfernt eine Erwerbspensum aus der Databse
	 *
	 * @param erwerbspensumContainerID der Entfernt werden soll
	 */
	void removeErwerbspensum(@Nonnull String erwerbspensumContainerID, Gesuch gesuch);

	/**
	 * Gibt zurück, ob fuer das uebergebene Gesuch ein Erwerbspensum erfasst werden muss.
	 * Ein Erwerbspensum muss fuer alle Gesuchsteller erfasst werden wenn es keine Fachstelle erfasst wurde und es
	 * kein Angebot des Types Tagesschule, TAGI oder Tageseltern für Schulkind gibt
	 */
	boolean isErwerbspensumRequired(@Nonnull Gesuch gesuch);
}
