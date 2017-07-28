package ch.dvbern.ebegu.services;

import java.util.concurrent.Future;

import javax.annotation.Nonnull;

/**
 * Service zum Ausfuehren von manuellen DB-Migrationen
 */
public interface DatabaseMigrationService {

	/**
	 * Fuehrt ein manuelles Script auf der Datenbank aus
	 */
	Future<Boolean> processScript(@Nonnull String scriptId);
}
