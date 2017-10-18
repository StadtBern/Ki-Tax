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

public enum JaxExternalFerienName {

	SOMMERFERIEN(1),
	HERBSTFERIEN(2),
	WEIHNACHTSFERIEN(3),
	FRUEHLINGSFERIEN(4);

	private final int ferienNr;

	JaxExternalFerienName(int ferienNr) {
		this.ferienNr = ferienNr;
	}

	public static JaxExternalFerienName getByFerienNr(int ferienNr) {
		for (JaxExternalFerienName o : values()) {
			if (o.ferienNr == ferienNr) {
				return o;
			}
		}
		return null;
	}
}
