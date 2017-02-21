package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Zahlungsauftrag;

/**
 * Service fuer Generierung des Zahlungsfile gemäss ISO200022
 */
public interface Pain001Service {

	byte[] getPainFileContent(Zahlungsauftrag zahlungsauftrag);

}
