package ch.dvbern.ebegu.rules;

/**
 * Dieses Enum definiert die moeglichen Regeln. Grundsaetzlich gibt es fuer jede Dokumentierte Regel
 * einen solchen RuleeKey. Dieser wird dann verwendet um allfaellige Abschnitt und Calc rules dieser Regel
 * zuzuordnen.
 */
public enum RuleKey {

	NO_RULE, // Verwendet für diverse Verarbeitungsschritte (z.B. Monatsschnitte machen)

	ERWERBSPENSUM,
	BETREUUNGSPENSUM,
	FACHSTELLE,
	/**
	 * Regel 4.2 definiert dass kein Anspruch besteht wenn das massgebende Einkommen zu hoch ist
	 */
	EINKOMMEN,


	ABWESENHEIT,
	BETREUUNGSANGEBOT_TYP,
	MINDESTALTER,
	WOHNSITZ,
	EINREICHUNGSFRIST,
	FAMILIENSITUATION,
	WOHNHAFT_IM_GLEICHEN_HAUSHALT
}
