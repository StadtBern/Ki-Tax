package ch.dvbern.ebegu.services;

/**
 * Service zum berechnen und speichern der Verfuegung
 */
public interface SchulungService {

	void resetSchulungsdaten();

	void deleteSchulungsdaten();

	void createSchulungsdaten();

	String[] getSchulungBenutzer();
}
