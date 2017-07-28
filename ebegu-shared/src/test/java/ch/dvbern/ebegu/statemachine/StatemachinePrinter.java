/*
 * Copyright (c) 2017 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 *
 */

package ch.dvbern.ebegu.statemachine;

import ch.dvbern.ebegu.enums.AntragEvents;
import ch.dvbern.ebegu.enums.AntragStatus;
import com.github.oxo42.stateless4j.StateMachineConfig;
import org.junit.Test;

public class StatemachinePrinter {

	@Test
	public void printStatemachine() {
		StateMachineConfigProducer stateMachineConfigProducer = new StateMachineConfigProducer();
		StateMachineConfig<AntragStatus, AntragEvents> config = stateMachineConfigProducer.createStateMachineConfig();
		StateMachineFactory.printStateMachineDocumentation(config);
	}

}
