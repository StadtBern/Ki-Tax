package ch.dvbern.ebegu.cdi;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Producer fuer den Logger. Gibt einen Logger fuer den Klassennamen des injection points zurueck
 */
public class LoggerProducer {

	@Produces
	public Logger createLogger(final InjectionPoint injectionPoint) {
		return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getSimpleName());
	}
}
