package ch.dvbern.ebegu.services;

import javax.annotation.Nonnull;

/**
 * Service zum Ausfuehren von manuellen DB-Migrationen
 */
public interface DatabaseMigrationService {

	/**
	 * Fuehrt ein manuelles Script auf der Datenbank aus
	 */
	void processScript(@Nonnull String scriptId);
}
