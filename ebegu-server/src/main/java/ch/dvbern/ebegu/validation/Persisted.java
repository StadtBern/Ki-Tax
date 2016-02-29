/*
 * Copyright © 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.validation;

/**
 * Felder mit dieser Gruppe werden nur validiert, wenn die Resource eigentlich schon in der DB sein muesste (i.E.: die ID ist schon vorhanden)
 */
@SuppressWarnings("InterfaceNeverImplemented") // Bean Validation only
public interface Persisted {
}
