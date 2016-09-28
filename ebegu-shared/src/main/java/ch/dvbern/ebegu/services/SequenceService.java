/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Sequence;
import ch.dvbern.ebegu.enums.SequenceType;

import javax.annotation.Nonnull;

public interface SequenceService {

	Long createNumberTransactional(@Nonnull SequenceType seq, Mandant mandant);

	Sequence initFallNrSeqMandant(@Nonnull Mandant mandant);
}
