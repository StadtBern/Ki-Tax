/*
 * Copyright (c) 2013 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.types.DateRange;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Interface fuer Konstanten.
 */
public interface Constants {

	int DB_DEFAULT_MAX_LENGTH = 255;
	int DB_TEXTAREA_LENGTH = 1000;
	int DB_DEFAULT_SHORT_LENGTH = 100;

	int UUID_LENGTH = 36;
	int PLZ_LENGTH = 4;

	int LOGIN_TIMEOUT_SECONDS = 60 * 60; //aktuell 1h
	int COOKIE_TIMEOUT_SECONDS = 60 * 60 * 12; //aktuell 12h

	Locale DEFAULT_LOCALE = new Locale("de", "CH");


	String REGEX_EMAIL = "[\\S\\-.]+@([\\S-]+\\.)+[\\S-]+";
	String REGEX_TELEFON = "(0|\\+41|0041)[ ]*[\\d]{2}[ ]*[\\d]{3}[ ]*[\\d]{2}[ ]*[\\d]{2}";
	String REGEX_TELEFON_MOBILE = "(0|\\+41|0041)[ ]*(74|75|76|77|78|79)[ ]*[\\d]{3}[ ]*[\\d]{2}[ ]*[\\d]{2}";
	String PATTERN_DATE = "dd.MM.yyyy";
	DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(Constants.PATTERN_DATE);

	String SERVER_MESSAGE_BUNDLE_NAME = "ch.dvbern.ebegu.i18n.server-messages";

	LocalDate END_OF_TIME = LocalDate.of(9999, 12, 31);
	LocalDate START_OF_TIME = LocalDate.of(1000, 1, 1);

	DateRange DEFAULT_GUELTIGKEIT = new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME);

	long MAX_TEMP_DOWNLOAD_AGE_MINUTES = 3;

	int FALLNUMMER_LENGTH = 6;
}
