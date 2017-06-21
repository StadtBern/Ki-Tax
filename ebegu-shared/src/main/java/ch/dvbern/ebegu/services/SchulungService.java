package ch.dvbern.ebegu.services;

/**
 * Service fuer erstellen und mutieren von Schulungsdaten
 */
public interface SchulungService {

	void resetSchulungsdaten();

	void deleteSchulungsdaten();

	void createSchulungsdaten();

	String[] getSchulungBenutzer();
}
