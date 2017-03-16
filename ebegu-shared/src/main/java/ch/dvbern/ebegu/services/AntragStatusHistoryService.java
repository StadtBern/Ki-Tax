package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.AntragStatusHistory;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Service zum Verwalten von AntragStatusHistory
 */
public interface AntragStatusHistoryService {

	/**
	 * Erstellt einen neuen Datensatz mit aktuellerStatus gleich wie der Status des uebergebenden Gesuchs
	 * und alterStatus, der Status der es vorher hatte. Der eingeloggte Benutzer wird als benutzer hinterlegt
	 * @param gesuch
	 * @return
	 */
	@Nonnull
	AntragStatusHistory saveStatusChange(@Nonnull Gesuch gesuch);

	/**
	 * Findet den letzten StatusChange furs gegebene Gesuch und gibt ihn zurueck
	 * @param gesuch
	 * @return
	 */
	@Nullable
	AntragStatusHistory findLastStatusChange(@Nonnull Gesuch gesuch);

	/**
	 * Entfernt alle AntrasStatusHistory Objekte vom gegebenen Gesuch
	 * @param gesuch
	 */
	void removeAllAntragStatusHistoryFromGesuch(Gesuch gesuch);

	@Nonnull
	Collection<AntragStatusHistory> findAllAntragStatusHistoryByGesuch(@Nonnull Gesuch gesuch);

	@Nonnull
	public Collection<AntragStatusHistory> findAllAntragStatusHistoryByGPFall(@Nonnull Gesuchsperiode gesuchsperiode, Fall fall);
}
