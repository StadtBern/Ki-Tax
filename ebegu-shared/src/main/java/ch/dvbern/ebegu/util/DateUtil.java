package ch.dvbern.ebegu.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utils fuer Date Elemente
 */
public class DateUtil {

	/**
	 * Parset den gegebenen String als LocalDate mit dem Format "yyyy-MM-dd"
	 * Sollte der gegebene String null oder leer sein, wird now() zurueckgegeben
	 * @param stringDate
	 * @return
	 */
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
	 * @param stringDateTime
	 * @return
	 */
	public static LocalDateTime parseStringToDateTimeOrReturnNow(@Nonnull String stringDateTime) {
		LocalDateTime date = LocalDateTime.now();
		if (!stringDateTime.isEmpty()) {
			date = LocalDateTime.parse(stringDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}
		return date;
	}
}
