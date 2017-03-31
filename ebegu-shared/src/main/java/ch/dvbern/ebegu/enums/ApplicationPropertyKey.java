/*
 * Copyright (c) 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.enums;

/**
 * Keys fuer die Application Properties die wir in der DB speichern
 */
public enum ApplicationPropertyKey {


	/**
	 * Wenn true gibt der Evaluator seine Debugmeldungen in das log aus.
	 */
	EVALUATOR_DEBUG_ENABLED,

	/**
	 * Damit wir Test/Produktion leichter unterscheiden koennen kann man die Hintergrundfarbe einstellen
	 */
	BACKGROUND_COLOR,

	/**
	 * Wenn eine Nachricht zu einem Fall eintrifft, welcher noch keinen Verantwortlichen hat, so soll diese an den
	 * Default-Verantwortlichen geschickt werden.
	 */
	DEFAULT_VERANTWORTLICHER,

	/**
	 * <Dbtr><Nm> Name des Zahlungspflichtigen
	 */
	DEBTOR_NAME,

	/**
	 * <IBAN> IBAN des Zahlungspflichtigen?
	 */
	DEBTOR_IBAN,

	/**
	 * <BIC> BIC des Zahlungspflichtigen?
	 */
	DEBTOR_BIC,

	/**
	 * <ChrgsAcct> <IBAN> IBAN Belastungskonto Gebühren?
	 */
	DEBTOR_IBAN_GEBUEHREN,

	/**
	 *  Anzahl Monate nach Erstellungsdatum bis der GS gewarnt wird, wenn er nicht freigibt
	 */
	ANZAHL_MONATE_BIS_WARNUNG_FREIGABE,

	/**
	 * Anzahl Monate nach Freigabe bis der GS gewarnt wird, wenn er Quittung nicht schickt
	 */
	ANZAHL_MONATE_BIS_WARNUNG_QUITTUNG,

	/**
	 * Anzahl Monate nach Warnung bis Gesuch geloescht wird, wenn er nicht freigibt
	 */
	ANZAHL_MONATE_BIS_LOESCHUNG_NACH_WARNUNG_FREIGABE,

	/**
	 * Anzahl Monate nach Warnung bis Gesuch geloescht wird, wenn er Quittung nicht schickt
	 */
	ANZAHL_MONATE_BIS_LOESCHUNG_NACH_WARNUNG_QUITTUNG;
}
