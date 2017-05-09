package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuch;

import javax.annotation.Nonnull;

/**
 * Interface um gewisse Services als SUPER_ADMIN aufrufen zu koennen
 */
public interface SuperAdminService {

	/**
	 * Entfernt ein Gesuch mit allen seinen Objekten. RunAs(SUPER_ADMIN)
	 * @param gesuchId
	 */
	void removeGesuch(@Nonnull String gesuchId);

	/**
	 * Speichert das Gesuch und speichert den Statuswechsel in der History falls saveInStatusHistory
	 * gesetzt ist.
	 */
	@Nonnull
	Gesuch updateGesuch(@Nonnull Gesuch gesuch, boolean saveInStatusHistory);
}
