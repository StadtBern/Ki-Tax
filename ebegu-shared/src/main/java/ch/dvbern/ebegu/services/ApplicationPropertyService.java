package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.ApplicationProperty;

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
	ApplicationProperty saveOrUpdateApplicationProperty(String key, String value);

	/**

	 * @param key name des Property
	 * @return Property mit demg egebenen key oder null falls nicht vorhanden
	 */
	ApplicationProperty readApplicationProperty(String key);

	/**
	 *
	 * @return Liste aller ApplicationProperties aus der DB
	 */
	Collection<ApplicationProperty> listApplicationProperties();

}
