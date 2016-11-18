package ch.dvbern.ebegu.entities;

/**
 * Damit wir entities mit Mandant alle gleich beahndeln koennen machen wir ein interface
 */
public interface HasMandant {

	/**
	 * gibt den zugeordneten Mandant zurueck
	 * @return
	 */
	Mandant getMandant();

	String getId();

}
