package ch.dvbern.ebegu.rules;

/**
 * Die Regeln lassen sich in verschiedene Typen einteilen,
 */
public enum RuleType {

	/**
	 * Typ fuer Regeln die nicht im normalen Regelsystem laufen sondern ausserhalb angestossen werden
	 */
	NO_RULE,
	/**
	 * Regel die dazu verwendet wird die Daten die fuer die Anspruch-Berechnung relevant sind zu setzen
	 */
	GRUNDREGEL_DATA,
	GRUNDREGEL_CALC,
	/**
	 * Typ fuer Regeln die keine neuen Daten berechnen oder Zeitabschnitte einfuegen sondern nur
	 * Reduktionen des Arbeitspensums durchfuehren.
	 */
	REDUKTIONSREGEL

}
