package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuch;

/**
 * Service zum berechnen und speichern der Verfuegung
 */
public interface TestfaelleService {

	StringBuilder createAndSaveTestfaelle(String fallid,
										  Integer iterationCount,
										  boolean betreuungenBestaetigt,
										  boolean verfuegen);

	Gesuch createAndSaveTestfaelle(String fallid,
										  boolean betreuungenBestaetigt,
										  boolean verfuegen);
}
