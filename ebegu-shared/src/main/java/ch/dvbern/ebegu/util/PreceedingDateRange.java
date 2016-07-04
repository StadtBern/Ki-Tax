package ch.dvbern.ebegu.util;

/**
 * Legt fest, was mit vorhergehenden Entities geschieht.
 * Voraussetzung: 2 Entities, die aneinander anschliessen.
 * Wird z.B. das 2. Entity geloescht, kann das Vorhergehende Entity entweder
 * - so bleiben wie es ist oder
 * - auf das GueltigBis-Datum des geloeschten Entities gesetzt werden
 */
public enum PreceedingDateRange {
	/**
	 * Die vorhergehende DateRange unveraendert lassen
	 */
	KEEP,
	/**
	 * Die vorhergehende DateRange erweitern auf das GueltigBis Datum des nachfolgenden (i.E.: geloeschten) Entity's.
	 */
	EXTEND_TO_DELETED
}
