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

/**
 * Class to store constants needed for batch jobs
 */
public final class WorkJobConstants {
	public static final String REPORT_VORLAGE_TYPE_PARAM = "reportVolageType";
	public static final String DATE_FROM_PARAM = "dateFrom";
	public static final String DATE_TO_PARAM = "dateTo";
	public static final String GESUCH_PERIODE_ID_PARAM = "gesuchPeriodeID";
	public static final String ZAHLUNGSAUFTRAG_ID_PARAM = "zahlungsauftragID";
	public static final String INKL_BG_GESUCHE = "inklBgGesuche";
	public static final String INKL_MISCH_GESUCHE = "inklMischGesuche";
	public static final String INKL_TS_GESUCHE = "InklTsGesuche";
	public static final String OHNE_ERNEUERUNGSGESUCHE = "OhneErneuerungsgesuche";
	public static final String TEXT = "text";
	public static final String EMAIL_OF_USER = "emailOfUser";

	private WorkJobConstants() {
	}
}
