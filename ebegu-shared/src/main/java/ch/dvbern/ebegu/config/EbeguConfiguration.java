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
	 * Gibt an ob die Client Applikation https verwendet. Wenn true werden cookies nur bei https clients gesetzt
	 */
	boolean isClientUsingHTTPS();

	/**
	 * Gibt zurueck, ob E-Mails versendet werden sollen. Falls nicht, wird der entsprechende Text auf der Console ausgegeben
	 */
	boolean isSendingOfMailsDisabled();

	/**
	 * Gibt einen Host zurück welcher zum Verschicken eines Mails verwendet wird.
	 *
	 * @return einen Hostnamen, oder {@code null}
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
	 * @return die Absender-Adresse oder {@code null}
	 */
	String getSenderAddress();

	/**
	 * Gibt den Hostname des Servers zurück.
	 *
	 * @return den Hostname oder {@code null}
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

	/**
	 * Property, welches festlegt, ob die vordefinierten Testfaelle fuer diese Umgebung verwendet werden duerfen.
	 * Achtung, dieses Property wird vom Dummy-Login Property übersteuert, d.h. es müssen beide gesetzt sein!
	 */
	boolean isTestfaelleEnabled();

	/**
	 * Admin-Email: An diese Adresse wird z.B. die Zahlungskontrolle gesendet.
	 */
	String getAdministratorMail();

	/**
	 * Email, deren Benutzer die Rolle SUER_ADMIN erhaelt.
	 */
	String getSuperuserMail();
}
