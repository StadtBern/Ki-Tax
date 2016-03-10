package ch.dvbern.ebegu.api.errors;

import ch.dvbern.ebegu.config.config.EbeguConfiguration;
import ch.dvbern.ebegu.util.Constants;
import org.jboss.resteasy.api.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;
import java.util.Locale;

/**
 * Created by imanol on 02.03.16.
 * Basis Exception Mapper
 * @see  <a href="https://samaxes.com/2014/04/jaxrs-beanvalidation-javaee7-wildfly/" >https://samaxes.com/2014/04/jaxrs-beanvalidation-javaee7-wildfly</a>
 */
public abstract class AbstractEbeguExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Context
	private HttpHeaders headers;

	@Inject
	protected EbeguConfiguration configuration;

	protected Response buildResponse(Object entity, String mediaType, Response.Status status) {
		Response.ResponseBuilder builder = Response.status(status).entity(entity);
		builder.type(MediaType.TEXT_PLAIN);
		builder.header(Validation.VALIDATION_HEADER, "true");
		return builder.build();
	}

	protected abstract Response buildViolationReportResponse(E exception, Response.Status status);

	protected String unwrapException(Throwable t) {
		StringBuffer sb = new StringBuffer();
		doUnwrapException(sb, t);
		return sb.toString();
	}

	/**
	 * unwrapped alle causes und fuegt sie zum Stringbuffer hinzu
	 * @param sb buffer to append to
	 * @param t throwable
	 */
	private void doUnwrapException(StringBuffer sb, Throwable t) {
		if (t == null) {
			return;
		}
		sb.append(t.toString());
		if (t.getCause() != null && t != t.getCause()) {
			sb.append('[');
			doUnwrapException(sb, t.getCause());
			sb.append(']');
		}
	}

	/**
	 *
	 * @param accept Liste mit Accepted media types
	 * @return Gibt den ersten von uns unterstuetzten MediaType zurueck
	 */
	@Nullable
	protected MediaType getAcceptMediaType(List<MediaType> accept) {
		for (MediaType mt : accept) {
			if (MediaType.APPLICATION_JSON_TYPE.getType().equals(mt.getType())
				&& MediaType.APPLICATION_JSON_TYPE.getSubtype().equals(mt.getSubtype())) {
				return MediaType.APPLICATION_JSON_TYPE;
			}
			if (MediaType.APPLICATION_XML_TYPE.getType().equals(mt.getType())
				&& MediaType.APPLICATION_XML_TYPE.getSubtype().equals(mt.getSubtype())) {
				return MediaType.APPLICATION_XML_TYPE;
			}
		}
		return null;
	}

	protected void logException(Exception exception) {
		LOG.warn("Exception occured: " ,exception);
	}


	protected Locale getLocaleFromHeader() {
		if (!headers.getAcceptableLanguages().isEmpty()) {
			return headers.getAcceptableLanguages().get(0);
		}
		return Constants.DEFAULT_LOCALE;

	}

}
