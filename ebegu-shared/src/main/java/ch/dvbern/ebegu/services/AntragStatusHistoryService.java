package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.AntragStatusHistory;
import ch.dvbern.ebegu.entities.Gesuch;

import javax.annotation.Nonnull;

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
}
