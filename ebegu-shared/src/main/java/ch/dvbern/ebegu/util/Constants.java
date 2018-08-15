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

package ch.dvbern.ebegu.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import ch.dvbern.ebegu.types.DateRange;

/**
 * Interface fuer Konstanten.
 */
public final class Constants {

	public static final int DB_DEFAULT_MAX_LENGTH = 255;
	public static final int DB_TEXTAREA_LENGTH = 4000;
	public static final int DB_DEFAULT_SHORT_LENGTH = 100;

	public static final int UUID_LENGTH = 36;

	public static final int LOGIN_TIMEOUT_SECONDS = 60 * 60; //aktuell 1h

	public static final int ABWESENHEIT_DAYS_LIMIT = 30;

	public static final int MAX_TIMEOUT_MINUTES = 360; // minutes
	public static final int STATISTIK_TIMEOUT_MINUTES = 180; // minutes

	public static final Locale DEFAULT_LOCALE = new Locale("de", "CH");

	public static final String DATA = "Data";
	public static final String REGEX_EMAIL = "[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}";
	public static final String REGEX_TELEFON = "(0|\\+41|0041)[ ]*[\\d]{2}[ ]*[\\d]{3}[ ]*[\\d]{2}[ ]*[\\d]{2}";
	public static final String REGEX_TELEFON_MOBILE = "(0|\\+41|0041)[ ]*(74|75|76|77|78|79)[ ]*[\\d]{3}[ ]*[\\d]{2}[ ]*[\\d]{2}";
	public static final String PATTERN_DATE = "dd.MM.yyyy";
	public static final String PATTERN_FILENAME_DATE_TIME = "dd.MM.yyyy_HH.mm.ss";
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(Constants.PATTERN_DATE);
	public static final DateTimeFormatter FILENAME_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.PATTERN_FILENAME_DATE_TIME);

	public static final DateTimeFormatter SQL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static final String SERVER_MESSAGE_BUNDLE_NAME = "ch.dvbern.ebegu.i18n.server-messages";
	public static final LocalDate END_OF_TIME = LocalDate.of(9999, 12, 31);
	public static final LocalDate START_OF_TIME = LocalDate.of(1000, 1, 1);

	public static final LocalDate GESUCHSPERIODE_17_18_AB = LocalDate.of(2017, 8, 1);
	public static final LocalDate GESUCHSPERIODE_17_18_BIS = LocalDate.of(2018, 7, 31);
	public static final DateRange GESUCHSPERIODE_17_18 = new DateRange(Constants.GESUCHSPERIODE_17_18_AB, Constants.GESUCHSPERIODE_17_18_BIS);

	public static final LocalDate GESUCHSPERIODE_18_19_AB = LocalDate.of(2018, 8, 1);
	public static final LocalDate GESUCHSPERIODE_18_19_BIS = LocalDate.of(2019, 7, 31);
	public static final DateRange GESUCHSPERIODE_18_19 = new DateRange(Constants.GESUCHSPERIODE_18_19_AB, Constants.GESUCHSPERIODE_18_19_BIS);

	public static final LocalDateTime START_OF_DATETIME = LocalDateTime.of(1000, 1, 1, 0, 0, 0);

	public static final DateRange DEFAULT_GUELTIGKEIT = new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME);

	public static final long MAX_SHORT_TEMP_DOWNLOAD_AGE_MINUTES = 3L;
	public static final long MAX_LONGER_TEMP_DOWNLOAD_AGE_MINUTES = 1440L; //24 * 60
	public static final int FALLNUMMER_LENGTH = 6;
	public static final long MAX_LUCENE_QUERY_RUNTIME = 500L;

	public static final int MAX_LUCENE_QUICKSEARCH_RESULTS = 25; // hier gibt es ein Problem, wenn wir fuer keines der Resultate berechtigt sind wird unser resultset leer sein auf client

	public static final String AUTH_TOKEN_SUFFIX_FOR_NO_TOKEN_REFRESH_REQUESTS = "NO_REFRESH";
	public static final String PATH_DESIGNATOR_NO_TOKEN_REFRESH = "notokenrefresh";

	public static final String TEMP_REPORT_FOLDERNAME = "tempReports";

	public static final String SYSTEM_USER_USERNAME = "System";

	private Constants() {
		//this prevents even the native class from
		//calling this ctor as well :
		throw new AssertionError();
	}

}
