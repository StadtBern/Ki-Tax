package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.Gesuchsteller;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Erwerbspensen
 */
public interface ErwerbspensumService {

	/**
	 * Speichert die Erwerbspensen neu in der DB falls der Key noch nicht existiert.
	 * @param erwerbspensumContainer Das Erwerbspensum das gespeichert werden soll
	 */
	@Nonnull
	ErwerbspensumContainer saveErwerbspensum(@Valid @Nonnull ErwerbspensumContainer erwerbspensumContainer);

	/**
	 * @param key PK (id) des ErwerbspensumContainers
	 * @return Optional mit dem  ErwerbspensumContainers mit fuer den gegebenen Key
	 */
	@Nonnull
	Optional<ErwerbspensumContainer> findErwerbspensum(@Nonnull String key);

	/**
	 * Sucht die Erwerbspensen des übergebenen Gesuchstellers.
     */
	Collection<ErwerbspensumContainer> findErwerbspensenForGesuchsteller(@Nonnull Gesuchsteller gesuchsteller);

	/**
	 * Sucht alle Erwerbspensen fuer das eingegebene Gesuch
	 * @param gesuchId
	 * @return
	 */
	Collection<ErwerbspensumContainer> findErwerbspensenFromGesuch(@Nonnull String gesuchId);

	/**
	 * @return Liste aller ErwerbspensumContainer aus der DB
	 */
	@Nonnull
	Collection<ErwerbspensumContainer> getAllErwerbspensenenContainer();

	/**
	 * entfernt eine Erwerbspensum aus der Databse
	 * @param erwerbspensumContainerID der Entfernt werden soll
	 */
	void removeErwerbspensum(@Nonnull String erwerbspensumContainerID);

}
