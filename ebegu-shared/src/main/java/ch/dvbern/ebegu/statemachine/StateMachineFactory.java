/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
			LOG.info("Printed State Machine documentation to {}", out.toAbsolutePath().toUri());

		} catch (IOException e) {
			LOG.error("Could not print state machine ", e);
		}

	}

}
