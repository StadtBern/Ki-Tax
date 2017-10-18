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

/**
 * Enum fuer the names of the Modul for Tagesschule.
 */
public enum JaxExternalModulName {

	VORMITTAG(1),
	MITTAG(2),
	MITTAG_HALB(3),
	NACHMITTAGS_1(4),
	NACHMITTAGS_1_HALB(5),
	NACHMITTAGS_2(6),
	NACHMITTAGS_2_HALB(7);

	private final int stufe;

	JaxExternalModulName(int stufe) {
		this.stufe = stufe;
	}

	public static JaxExternalModulName getByStufe(int stufe) {
		for (JaxExternalModulName o : values()) {
			if (o.stufe == stufe) {
				return o;
			}
		}
		return null;
	}
}
