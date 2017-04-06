/*
 * Copyright (c) 2013 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.config;


/**
 * Konfiguration von Kurstool
 */
public interface EbeguConfiguration {

	/**
	 * @return true wenn sich die Applikation im Entwiklungsmodus befindet, false sonst
	 */
	boolean getIsDevmode();

	String getDocumentFilePath();

	/**
	 * Gibt den (servlet-context relativen) path zur fedlet configuration zurueck die verwendet werden soll, die Idee
	 * waere das entweder dieser Path gesetzt ist oder einzelne properties
	 */
	String getFedletConfigPath();


	/**
	 * Gibt an ob die Client Applikation https verwendet. Wenn true werden cookies nur bei https clients gesetzt
	 */
	boolean isClientUsingHTTPS();

	/**
	 * Gibt die URL der IDM Rest Schnittstelle zureuck
	 */
	String getOpenIdmURL();


	/**
	 * Gibt die URL der IDM Rest Schnittstelle zureuck
	 */
	String getOpenamURL();

	/**
	 * Gibt den Benutzer fuer die IDM Rest Schnittstelle zurueck
	 */
	String getOpenIdmUser();

	/**
	 * Gibt das Passwort fuer die IDM Rest Schnittstelle zurueck
	 */
	String getOpenIdmPassword();


	/**
	 * Gibt zurueck ob die synchronisierung mit der IDM Rest Schnittstelle fuer Inst und Traegerschaft aktiviert ist
	 */
	boolean getOpenIdmEnabled();

	/**
	 * Set false on standaloneFile to use old communication with password and username for Testserver
	 */
	boolean getLoginWithToken();

	/**
	 * Gibt zurueck, ob E-Mails versendet werden sollen. Falls nicht, wird der entsprechende Text auf der Console ausgegeben
	 */
	boolean isSendingOfMailsDisabled();

	/**
	 * Gibt einen Host zurück welcher zum Verschicken eines Mails verwendet wird.
	 * @return einen Hostnamen, oder <code>null</code>
	 */
	String getSMTPHost();

	/**
	 * Gibt den Port zurück welcher zum Verschicken eines Mails verwendet wird.
	 * @return einen Port, 25 wenn nichts konfiguriert.
	 */
	int getSMTPPort();

	/**
	 * Gibt die Absender-Adresse fuer mails zurück.
	 * @return die Absender-Adresse oder <code>null</code>
	 */
	String getSenderAddress();

	/**
	 * Gibt den Hostname des Servers zurück.
	 * @return den Hostname oder <code>null</code>
	 */
	String getHostname();

	/**
	 * Gibt zurueck ob es moeglich sein soll mit den dummy useren einzulaggen
	 * @see "dummy-users.properties" and AuthResource#login
	 * @return true oder false
	 */
	boolean isDummyLoginEnabled();

	/**
	 * Wir definieren einen Benutzernamen dem wir IMMER die Rolle UserRole#SUPER_ADMIN zuweisen wenn er sich ueber IAM einloggt.
	 * Der Zweck dieses Users ist, dass wir ihn verwenden koennen um Supportrequests zu reproduzieren etc
	 * @return Name des SuperUsers
	 */
	String getEmailOfSuperUser();


	/**
	 * Hiermit kann die Hintergrundfarbe auf verschiedenen System per property eingestellt werden. Das sollte es uns ermoeglichen
	 * Test und Produktion besser unterscheiden zu koennen
	 * @return
	 */
	String getBackgroundColor();

	/**
	 * @return true wenn sich die Applikation im Testmodus fuer Zahlungen befindet, false sonst
	 */
	boolean getIsZahlungenTestMode();

	/**
	 * Gibt zurueck, ob der Dummy-Service für die EWK-Abfragen benutzt werden soll.
	 */
	boolean isPersonenSucheDisabled();

	/**
	 * Gibt den Endpoint des EWK-Services zurueck.
	 */
	String getPersonenSucheEndpoint();

	/**
	 * Gibt den Usernamen für den EWK-Service zurueck.
	 */
	String getPersonenSucheUsername();

	/**
	 * Gibt das Passwort für den EWK-Service zurueck.
	 */
	String getPersonenSuchePassword();
}
