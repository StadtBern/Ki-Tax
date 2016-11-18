package ch.dvbern.ebegu.entities;

import javax.annotation.Nonnull;

/**
 * Damit wir entities mit Mandant alle gleich beahndeln koennen machen wir ein interface
 */
public interface HasMandant {

	/**
	 * gibt den zugeordneten Mandant zurueck
	 * @return
	 */
	@Nonnull
	Mandant getMandant();

	@Nonnull
	String getId();

}
