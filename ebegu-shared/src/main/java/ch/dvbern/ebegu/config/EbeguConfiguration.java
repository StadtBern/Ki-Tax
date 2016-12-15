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
}
