package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Service zum Verwalten von AntragStatusHistory
 */
public interface AntragStatusHistoryService {

	/**
	 * Erstellt einen neuen Datensatz mit aktuellerStatus gleich wie der Status des uebergebenden Gesuchs
	 * und alterStatus, der Status der es vorher hatte. Wenn saveAsUser null ist wird der eingeloggte Benutzer
	 * wird als benutzer hinterlegt, sonst wird saveAsUser als benutzer hinterlegt
	 * @param gesuch
	 * @param saveAsUser
	 * @return
	 */
	@Nonnull
	AntragStatusHistory saveStatusChange(@Nonnull Gesuch gesuch, @Nullable Benutzer saveAsUser);

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
	Collection<AntragStatusHistory> findAllAntragStatusHistoryByGPFall(@Nonnull Gesuchsperiode gesuchsperiode, Fall fall);

	/**
	 * Prueft dass die letzte Statusaenderung auf BESCHWERDE_HAENGIG war und dann gibt die vorletzte Statusaenderung zurueck.
	 */
	AntragStatusHistory findLastStatusChangeBeforeBeschwerde(Gesuch gesuch);
}
