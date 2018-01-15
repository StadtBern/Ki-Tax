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

package ch.dvbern.ebegu.enums;

import java.util.Arrays;
import java.util.List;

public enum UserRole {
	SUPER_ADMIN,
	ADMIN,
	SACHBEARBEITER_JA,
	SACHBEARBEITER_TRAEGERSCHAFT,
	SACHBEARBEITER_INSTITUTION,
	JURIST,
	REVISOR,
	STEUERAMT,
	ADMINISTRATOR_SCHULAMT,
	SCHULAMT,
	GESUCHSTELLER;


	public boolean isRoleSchulamt() {
		return ADMINISTRATOR_SCHULAMT == this || SCHULAMT == this;
	}

	public boolean isRoleJugendamt() {
		return ADMIN == this || SACHBEARBEITER_JA == this;
	}

	public boolean isSuperadmin() {
		return SUPER_ADMIN == this;
	}

	public static List<UserRole> getSchulamtRoles() {
		return Arrays.asList(ADMINISTRATOR_SCHULAMT, SCHULAMT);
	}

	public static List<UserRole> getJugendamtRoles() {
		return Arrays.asList(ADMIN, SACHBEARBEITER_JA);
	}
}
