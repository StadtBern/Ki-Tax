package ch.dvbern.ebegu.api.util;

import net.bull.javamelody.MonitoringFilter;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * Created by imanol on 02.03.16.
 *
 * In REST-Resourcen enthaelt die URL immer auch irgendeine Business-ID, z.B. /api/v1/kinder/123/foo/bar
 * Fuers Monitoring ist die ID (hier: 123) aber nicht relevant und soll durch einen Platzhalter ersetzt werden.
 */
public class EbeguMonitoringFilter extends MonitoringFilter {
	private static final Pattern ID_PATTERN = Pattern.compile("/\\d+");
	private static final Pattern UUID_PATTERN = Pattern.compile("/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", Pattern.CASE_INSENSITIVE);

	@Nonnull
	@Override
	protected String getRequestName(@Nonnull HttpServletRequest request) {
		String defaultName = super.getRequestName(request);
		String name = UUID_PATTERN.matcher(defaultName).replaceAll("/{uuid}");
		name = ID_PATTERN.matcher(name).replaceAll("/{id}");
		return name;
	}
}

