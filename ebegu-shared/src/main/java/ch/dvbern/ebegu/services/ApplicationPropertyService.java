package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.ApplicationProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;


/**
 * Service zum Verwalten von Application Properties
 */
public interface ApplicationPropertyService {

	/**
	 * Speichert das property neu in der DB falls der Key noch nicht existeirt. Ansonsten wird ein neues Property mit diesem
	 * Key erstellt
	 * @param key name des Property
	 * @param value Wert des Property
	 * @return ApplicationProperty mit key und value
	 */
	@Nonnull
	ApplicationProperty saveOrUpdateApplicationProperty(@Nonnull String key, @Nonnull String value);

	/**

	 * @param key name des Property
	 * @return Property mit demg egebenen key oder null falls nicht vorhanden
	 */
	@Nullable
	ApplicationProperty readApplicationProperty(@Nonnull String key);

	/**
	 *
	 * @return Liste aller ApplicationProperties aus der DB
	 */
	Collection<ApplicationProperty> listApplicationProperties();

	/**
	 * removs an Application Property From the Databse
	 * @param testKey
	 */
	void removeApplicationProperty(@Nonnull String testKey);
}
