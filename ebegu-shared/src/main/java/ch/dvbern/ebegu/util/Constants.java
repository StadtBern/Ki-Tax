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
public interface Constants {

	int DB_DEFAULT_MAX_LENGTH = 255;
	int DB_TEXTAREA_LENGTH = 4000;
	int DB_DEFAULT_SHORT_LENGTH = 100;

	int UUID_LENGTH = 36;
	int PLZ_LENGTH = 4;

	int LOGIN_TIMEOUT_SECONDS = 60 * 60; //aktuell 1h

	int ABWESENHEIT_DAYS_LIMIT = 30;

	int MAX_TIMEOUT_MINUTES = 360; // minutes
	int STATISTIK_TIMEOUT_MINUTES = 40; // minutes

	Locale DEFAULT_LOCALE = new Locale("de", "CH");

	String DATA = "Data";
	String REGEX_EMAIL = "[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}";
	String REGEX_TELEFON = "(0|\\+41|0041)[ ]*[\\d]{2}[ ]*[\\d]{3}[ ]*[\\d]{2}[ ]*[\\d]{2}";
	String REGEX_TELEFON_MOBILE = "(0|\\+41|0041)[ ]*(74|75|76|77|78|79)[ ]*[\\d]{3}[ ]*[\\d]{2}[ ]*[\\d]{2}";
	String PATTERN_DATE = "dd.MM.yyyy";
	String PATTERN_FILENAME_DATE_TIME = "dd.MM.yyyy_HH.mm.ss";
	DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(Constants.PATTERN_DATE);
	DateTimeFormatter FILENAME_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.PATTERN_FILENAME_DATE_TIME);
	DateTimeFormatter SQL_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	DateTimeFormatter SQL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	String SERVER_MESSAGE_BUNDLE_NAME = "ch.dvbern.ebegu.i18n.server-messages";
	LocalDate END_OF_TIME = LocalDate.of(9999, 12, 31);
	LocalDate START_OF_TIME = LocalDate.of(1000, 1, 1);

	LocalDate GESUCHSPERIODE_17_18_AB = LocalDate.of(2017, 8, 1);
	LocalDate GESUCHSPERIODE_17_18_BIS = LocalDate.of(2018, 7, 31);
	DateRange GESUCHSPERIODE_17_18 = new DateRange(Constants.GESUCHSPERIODE_17_18_AB, Constants.GESUCHSPERIODE_17_18_BIS);

	LocalDate GESUCHSPERIODE_18_19_AB = LocalDate.of(2018, 8, 1);
	LocalDate GESUCHSPERIODE_18_19_BIS = LocalDate.of(2019, 7, 31);
	DateRange GESUCHSPERIODE_18_19 = new DateRange(Constants.GESUCHSPERIODE_18_19_AB, Constants.GESUCHSPERIODE_18_19_BIS);

	LocalDateTime START_OF_DATETIME = LocalDateTime.of(1000, 1, 1, 0, 0, 0);

	DateRange DEFAULT_GUELTIGKEIT = new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME);

	long MAX_TEMP_DOWNLOAD_AGE_MINUTES = 3L;
	int FALLNUMMER_LENGTH = 6;
	long MAX_LUCENE_QUERY_RUNTIME = 500L;

	int MAX_LUCENE_QUICKSEARCH_RESULTS = 25; // hier gibt es ein Problem, wenn wir fuer keines der Resultate berechtigt sind wird unser resultset leer sein auf client

	String DEFAULT_MANDANT_ID = "e3736eb8-6eef-40ef-9e52-96ab48d8f220";
	String AUTH_TOKEN_SUFFIX_FOR_NO_TOKEN_REFRESH_REQUESTS = "NO_REFRESH";
	String PATH_DESIGNATOR_NO_TOKEN_REFRESH = "notokenrefresh";
}
