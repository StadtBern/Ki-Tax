package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;

import java.util.List;

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
