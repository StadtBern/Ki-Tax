/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
	 *
	 * @return einen Hostnamen, oder <code>null</code>
	 */
	String getSMTPHost();

	/**
	 * Gibt den Port zurück welcher zum Verschicken eines Mails verwendet wird.
	 *
	 * @return einen Port, 25 wenn nichts konfiguriert.
	 */
	int getSMTPPort();

	/**
	 * Gibt die Absender-Adresse fuer mails zurück.
	 *
	 * @return die Absender-Adresse oder <code>null</code>
	 */
	String getSenderAddress();

	/**
	 * Gibt den Hostname des Servers zurück.
	 *
	 * @return den Hostname oder <code>null</code>
	 */
	String getHostname();

	/**
	 * Gibt zurueck ob es moeglich sein soll mit den dummy useren einzulaggen
	 *
	 * @return true oder false
	 * @see "dummy-users.properties" and AuthResource#login
	 */
	boolean isDummyLoginEnabled();

	/**
	 * Wir definieren einen Benutzernamen dem wir IMMER die Rolle UserRole#SUPER_ADMIN zuweisen wenn er sich ueber IAM einloggt.
	 * Der Zweck dieses Users ist, dass wir ihn verwenden koennen um Supportrequests zu reproduzieren etc
	 *
	 * @return Name des SuperUsers
	 */
	String getEmailOfSuperUser();

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
	 * URL des WSDLs des EWK-Services
	 */
	String getPersonenSucheWsdl();

	/**
	 * Gibt den Usernamen für den EWK-Service zurueck.
	 */
	String getPersonenSucheUsername();

	/**
	 * Gibt das Passwort für den EWK-Service zurueck.
	 */
	String getPersonenSuchePassword();

	/**
	 * Gibt die URL des API Endpunkt des LoginConnectors zurueck.
	 * Ueber diesen list  KI-TAX die URLS zum single-login und single-logout
	 *
	 * @return REST API Endpunkt ueber den Ki-TAX die URLS fuer login/logout requests lesen kann
	 */
	String getLoginProviderAPIUrl();

	/**
	 * @return true if LoginConnector may access REST interface remotly, otherwise only local access is allowed
	 */
	boolean isRemoteLoginConnectorAllowed();

	/**
	 * @return den Benutzernamen des internen API users
	 */
	String getInternalAPIUser();

	/**
	 * @return das Benutzerpasswort fuer den internen API USER
	 */
	String getInternalAPIPassword();

	/**
	 * @return den Benutzernamen des Schulamt API users
	 */
	String getSchulamtAPIUser();

	/**
	 * @return das Benutzerpasswort fuer den Schulamt API USER
	 */
	String getSchulamtAPIPassword();

	/**
	 * @return by default the secure flag of cookies will be set based on the incoming request. To force the application
	 * to only set cookies with the secure flag this property can be set to true (default is false)
	 */
	boolean forceCookieSecureFlag();

	/**
	 * @return ob die Asynchron generierten Reports als attachments direkt im Infomail an den auftraggebendne Benutzer angehaengt werden
	 */
	boolean isSendReportAsAttachement();

}
