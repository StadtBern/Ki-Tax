/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dient dazu die Aufgabentypen zu unterscheiden
 */
public enum WorkJobType {

	REPORT_GENERATION(
		WorkJobConstants.REPORT_VORLAGE_TYPE_PARAM,
		WorkJobConstants.DATE_FROM_PARAM,
		WorkJobConstants.DATE_TO_PARAM,
		WorkJobConstants.GESUCH_PERIODE_ID_PARAM,
		WorkJobConstants.INKL_BG_GESUCHE,
		WorkJobConstants.INKL_MISCH_GESUCHE,
		WorkJobConstants.INKL_TS_GESUCHE,
		WorkJobConstants.OHNE_ERNEUERUNGSGESUCHE,
		WorkJobConstants.TEXT);

	List<String> paramNames = new ArrayList<>();


	WorkJobType(String... parameters) {
		paramNames.addAll(Arrays.asList(parameters));
	}

	public List<String> getParamNames() {
		return paramNames;
	}
}
