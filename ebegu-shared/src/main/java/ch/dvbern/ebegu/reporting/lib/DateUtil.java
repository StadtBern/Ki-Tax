package ch.dvbern.ebegu.reporting.lib;

import java.time.format.DateTimeFormatter;

/**
 * Utils fuer Date Elemente
 */
public class DateUtil {

	public static final DateTimeFormatter SQL_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static final DateTimeFormatter SQL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

}
