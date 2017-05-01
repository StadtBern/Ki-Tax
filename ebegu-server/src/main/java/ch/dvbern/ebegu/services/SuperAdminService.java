package ch.dvbern.ebegu.services;

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
}
