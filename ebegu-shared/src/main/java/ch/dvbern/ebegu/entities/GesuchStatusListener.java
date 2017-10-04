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

package ch.dvbern.ebegu.entities;

import java.util.List;

import javax.persistence.PostLoad;
import javax.persistence.PreUpdate;

import ch.dvbern.ebegu.enums.AntragEvents;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.statemachine.StateMachineConfigProducer;
import ch.dvbern.ebegu.statemachine.StateMachineFactory;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.ebegu.statemachine.GesuchEventWithParam.getTrigger;

/**
 * At the moment there are no events fired on EBEGU. In order to nevertheless be able to use this statemachine, we just
 * check all possible transitions from current state. If the required state transitions not our statemachine we throw an error.
 * TODO: This part of the code must replaced be events!
 */
public class GesuchStatusListener {

	private static final Logger LOG = LoggerFactory.getLogger(GesuchStatusListener.class);

	private StateMachineConfig<AntragStatus, AntragEvents> config;

	@PostLoad
	public void postLoad(Gesuch gesuch) {
		gesuch.setPreStatus(gesuch.getStatus());
	}

	@PreUpdate
	public void preUpdate(Gesuch gesuch) {

		if (gesuch.getPreStatus() != null && !gesuch.getPreStatus().equals(gesuch.getStatus())) {

			final AntragStatus preStatus = gesuch.getPreStatus();
			final AntragStatus postStatus = gesuch.getStatus();

			gesuch.setStatus(preStatus);

			StateMachine<AntragStatus, AntragEvents> stateMachine = StateMachineFactory.getStateMachine(gesuch, getConfig());
			final List<AntragEvents> permittedTriggers = stateMachine.getPermittedTriggers();

			for (AntragEvents permittedTrigger : permittedTriggers) {
				stateMachine.fire(getTrigger(permittedTrigger), gesuch);
				if (stateMachine.getState().equals(postStatus)) {
					gesuch.setStatus(postStatus);
					return;
				}
				stateMachine = StateMachineFactory.getStateMachine(gesuch, getConfig());
			}

			LOG.error("State Machine received unhandled transition from {} for current state {}", preStatus, postStatus);
			throw new EbeguRuntimeException("handleFSMEvent", ErrorCodeEnum.ERROR_INVALID_EBEGUSTATE, postStatus);
		}
	}

	private StateMachineConfig<AntragStatus, AntragEvents> getConfig() {
		if (config == null) {
			/*Not working at the moment, No injection possible at the moment!!!*/
			/*config = CDI.current().select(StateMachineConfig.class.getAnnotation(AntragStatus.class)).get();*/

			StateMachineConfigProducer stateMachineConfigProducer = new StateMachineConfigProducer();
			config = stateMachineConfigProducer.createStateMachineConfig();
		}
		return config;
	}

}
