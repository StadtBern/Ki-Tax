package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Zahlungsauftrag;

/**
 * Service fuer Generierung des Zahlungsfile gemäss ISO200022
 */
public interface Pain001Service {

	String SCHEMA_NAME = "pain.001.001.03.ch.02.xsd";
	String SCHEMA_LOCATION_LOCAL = "ch.dvbern.ebegu.iso20022.V03CH02/" + SCHEMA_NAME;
	String SCHEMA_LOCATION = "http://www.six-interbank-clearing.com/de/" + SCHEMA_NAME;

	byte[] getPainFileContent(Zahlungsauftrag zahlungsauftrag);

}
