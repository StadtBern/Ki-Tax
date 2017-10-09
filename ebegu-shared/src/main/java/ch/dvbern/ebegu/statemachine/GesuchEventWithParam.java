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

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.AntragEvents;
import ch.dvbern.ebegu.enums.AntragStatus;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;

/**
 * Um Typesafety der Parameter zu garantieren ist diese Klasse noetig
 */
public final class GesuchEventWithParam extends TriggerWithParameters1<Gesuch, AntragStatus, AntragEvents> {
	private GesuchEventWithParam(AntragEvents underlyingTrigger) {
		super(underlyingTrigger, Gesuch.class);
	}

	public static GesuchEventWithParam getTrigger(AntragEvents event) {
		return new GesuchEventWithParam(event);
	}
}

