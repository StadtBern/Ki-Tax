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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.AntragEvents;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for StateMachine
 */
public final class StateMachineFactory {

	private static final Logger LOG = LoggerFactory.getLogger(StateMachineFactory.class.getSimpleName());

	private StateMachineFactory() {
	}

	public static StateMachine<AntragStatus, AntragEvents> getStateMachine(
		@Nonnull Gesuch gesuch,
		@Nonnull StateMachineConfig<AntragStatus, AntragEvents> config) {
		Validate.notNull(gesuch);
		Validate.notNull(config);

		StateMachine<AntragStatus, AntragEvents> gesuchFiniteStateMachine =
			new StateMachine<>(gesuch.getStatus(), config);
		gesuchFiniteStateMachine.onUnhandledTrigger((antragStatus, antragEvent) -> {

			LOG.error("State Machine received unhandled event ({}) for current state {}", antragEvent, antragStatus);
			throw new EbeguRuntimeException("handleFSMEvent", ErrorCodeEnum.ERROR_INVALID_EBEGUSTATE, antragStatus);
		});

		return gesuchFiniteStateMachine;

	}

	public static void printStateMachineDocumentation(StateMachineConfig<AntragStatus, AntragEvents> config) {
		Path out = Paths.get("stateMachineDocumentation.dot");

		try {
			OutputStream outputStream = Files.newOutputStream(out);
			config.generateDotFileInto(outputStream);
			LOG.info("Printed State Machine documentation to " + out.toAbsolutePath().toUri());

		} catch (IOException e) {
			LOG.error("Could not print state machine ", e);
		}

	}

}
