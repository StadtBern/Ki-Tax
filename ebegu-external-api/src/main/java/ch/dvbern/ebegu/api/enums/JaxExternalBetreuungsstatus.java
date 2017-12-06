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

package ch.dvbern.ebegu.api.enums;

public enum JaxExternalBetreuungsstatus {

	//TODO (team) werden von Scolaris alle Betreuungsstatus benötigt?

	SCHULAMT_ANMELDUNG_ERFASST,
	SCHULAMT_ANMELDUNG_AUSGELOEST,
	SCHULAMT_ANMELDUNG_UEBERNOMMEN,
	SCHULAMT_ANMELDUNG_ABGELEHNT,
	SCHULAMT_FALSCHE_INSTITUTION;
}
