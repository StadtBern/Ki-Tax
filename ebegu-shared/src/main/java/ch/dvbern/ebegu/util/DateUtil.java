package ch.dvbern.ebegu.util;

import javax.annotation.Nullable;
import java.time.LocalDate;
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
}
