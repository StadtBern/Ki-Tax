package ch.dvbern.ebegu.rules;

/**
 * User: homa
 * Date: 17.06.16
 * comments homa
 */
public enum RuleKey {

	NO_RULE, // Verwendet für diverse Verarbeitungsschritte (z.B. Monatsschnitte machen)

	ERWERBSPENSUM,
	BETREUUNGSPENSUM,
	FACHSTELLE,
	/**
	 * Regel 4.2 definiert dass kein Anspruch besteht wenn das massgebende Einkommen zu hoch ist
	 */
	MAXIMALES_EINKOMMEN,


	ABWESENHEIT,
	BETREUUNGSANGEBOT_TYP,
	MINDESTALTER,
	WOHNSITZ,
	EINREICHUNGSFRIST,
	FAMILIENSITUATION;
}
