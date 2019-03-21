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

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.lib.date.feiertage.FeiertageHelper;

import static java.util.Objects.requireNonNull;

/**
 * Utils fuer Date Elemente
 */
public final class DateUtil {

	private DateUtil() {
	}

	/**
	 * Parset den gegebenen String als LocalDate mit dem Format "yyyy-MM-dd"
	 * Sollte der gegebene String null oder leer sein, wird now() zurueckgegeben
	 */
	@Nonnull
	public static LocalDate parseStringToDateOrReturnNow(@Nullable String stringDate) {
		LocalDate date = LocalDate.now();
		if (stringDate != null && !stringDate.isEmpty()) {
			date = parseStringToDate(stringDate);
		}
		return date;
	}

	@Nonnull
	public static LocalDate parseStringToDate(@Nonnull String stringDate) {
		requireNonNull(stringDate);
		return LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	/**
	 * Parset den gegebenen String als LocalDateTime mit dem Format "yyyy-MM-dd HH:mm:ss"
	 * Sollte der gegebene String null oder leer sein, wird now() zurueckgegeben
	 */
	@Nonnull
	public static LocalDateTime parseStringToDateTimeOrReturnNow(@Nonnull String stringDateTime) {
		LocalDateTime date = LocalDateTime.now();
		if (!stringDateTime.isEmpty()) {
			date = LocalDateTime.parse(stringDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}
		return date;
	}

	public static boolean isWeekend(@Nonnull LocalDate date) {
		return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
	}

	public static boolean isHoliday(@Nonnull LocalDate date) {
		return FeiertageHelper.isFeiertag_CH(Date.valueOf(date));
	}

	public static LocalDate getMax(@Nonnull LocalDate date1, @Nonnull LocalDate date2) {
		return date1.isAfter(date2) ? date1 : date2;
	}

	public static LocalDate getMin(@Nonnull LocalDate date1, @Nonnull LocalDate date2) {
		return date1.isBefore(date2) ? date1 : date2;
	}
}
