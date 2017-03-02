package ch.dvbern.ebegu.services;

/**
 * Service fuer diverse Admin-Aufgaben.
 * Im Moment nur fuer internen Gebrauch, d.h. die Methoden werden nirgends im Code aufgerufen, koennen aber bei Bedarf
 * schnell irgendwo angehaengt werden.
 */
public interface AdministrationService {

	String MANDANT_ID_BERN = "e3736eb8-6eef-40ef-9e52-96ab48d8f220";

	int COL_TRAEGERSCHAFT_ID = 0;
	int COL_TRAEGERSCHAFT_NAME = 1;
	int COL_TRAEGERSCHAFT_MAIL = 2;
	int COL_INSTITUTION_ID = 3;
	int COL_INSTITUTION_NAME = 4;
	int COL_STRASSE = 5;
	int COL_HAUSNUMMER = 6;
	int COL_PLZ = 7;
	int COL_ORT = 8;
	int COL_ZUSATZZEILE = 9;
	int COL_INSTITUTION_MAIL = 10;
	int COL_STAMMDATEN_ID = 11;
	int COL_ANGEBOT = 12;
	int COL_IBAN = 13;
	int COL_OEFFNUNGSSTUNDEN = 14;
	int COL_OEFFNUNGSTAGE = 15;


	/**
	 * Erstellt ein SQL-Skript mit Inserts/Updates.
	 * Input ist ein Excel, welches mit den aktuell vorhandenen Daten verglichen wird
	 */
	void createSQLSkriptInstutionsstammdaten();

	/**
	 * Erstellt ein Excel der aktuell vorhandenen Daten.
	 */
	void exportInstitutionsstammdaten();
}
