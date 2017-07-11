package ch.dvbern.ebegu.entities;

import javax.annotation.Nullable;

/**
 * Damit wir entities mit Gesuch alle gleich beahndeln koennen machen wir ein interface
 */
public interface HasGesuch {

	/**
	 * Gibt das verknuepfte Gesuch zurueck
	 */
	@Nullable
	Gesuch getGesuch();
}
