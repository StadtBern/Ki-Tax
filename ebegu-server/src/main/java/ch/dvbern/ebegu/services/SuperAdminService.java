package ch.dvbern.ebegu.services;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;

/**
 * Interface um gewisse Services als SUPER_ADMIN aufrufen zu koennen
 */
public interface SuperAdminService {

	/**
	 * Entfernt ein Gesuch mit allen seinen Objekten. RunAs(SUPER_ADMIN)
	 */
	void removeGesuch(@Nonnull String gesuchId);

	/**
	 * Entfernt einen Fall mitsamt seinen Gesuchen. RunAs(SUPER_ADMIN)
	 */
	void removeFall(@Nonnull Fall fall);

	/**
	 * Speichert das Gesuch und speichert den Statuswechsel in der History falls saveInStatusHistory
	 * gesetzt ist.
	 */
	@Nonnull
	Gesuch updateGesuch(@Nonnull Gesuch gesuch, boolean saveInStatusHistory, Benutzer saveAsUser);
}
