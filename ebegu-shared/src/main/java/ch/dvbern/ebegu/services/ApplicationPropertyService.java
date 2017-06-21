package ch.dvbern.ebegu.services;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.ApplicationProperty;
import ch.dvbern.ebegu.enums.ApplicationPropertyKey;


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
	ApplicationProperty saveOrUpdateApplicationProperty(@Nonnull ApplicationPropertyKey key, @Nonnull String value);

	/**

	 * @param key name des Property
	 * @return Property mit demg egebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<ApplicationProperty> readApplicationProperty(@Nonnull ApplicationPropertyKey key);

	/**
	 * Versucht den uebergebenen String in einene  key umzuwandeln und gibt dann das ensprechende property zurueck.
	 * Wenn der String keinem key enspricht exception
	 */
	Optional<ApplicationProperty> readApplicationProperty(String keyParam);

	/**
	 *
	 * @return Liste aller ApplicationProperties aus der DB
	 */
	@Nonnull
	Collection<ApplicationProperty> getAllApplicationProperties();

	/**
	 * removs an Application Property From the Databse
	 */
	void removeApplicationProperty(@Nonnull ApplicationPropertyKey testKey);

	/**
	 * Sucht das Property mit dem uebergebenen Key und gibt dessen Wert als String zurueck.
	 */
	@Nullable
	String findApplicationPropertyAsString(@Nonnull ApplicationPropertyKey name);

	/**
	 * Sucht das Property mit dem uebergebenen Key und gibt dessen Wert als BigDecimal zurueck.
	 */
	@Nullable
	BigDecimal findApplicationPropertyAsBigDecimal(@Nonnull ApplicationPropertyKey name);

	/**
	 * Sucht das Property mit dem uebergebenen Key und gibt dessen Wert als Integer zurueck.
	 */
	@Nullable
	Integer findApplicationPropertyAsInteger(@Nonnull ApplicationPropertyKey name);

	/**
	 * Sucht das Property mit dem uebergebenen Key und gibt dessen Wert als Boolean zurueck.
	 */
	@Nullable
	Boolean findApplicationPropertyAsBoolean(@Nonnull ApplicationPropertyKey name);

	/**
	 * Sucht das Property mit dem uebergebenen Key und gibt dessen Wert als Boolean zurueck.
	 * Falls das Property nicht gefunden wird, wird defaultValue zurueckgegeben.
	 */
	@Nonnull
	Boolean findApplicationPropertyAsBoolean(@Nonnull ApplicationPropertyKey name, boolean defaultValue);
}
