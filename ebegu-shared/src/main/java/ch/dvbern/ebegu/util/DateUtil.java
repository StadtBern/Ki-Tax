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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
			date = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		return date;
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

	public static boolean isWeekday(@Nonnull LocalDate date) {
		return date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY);
	}
}
