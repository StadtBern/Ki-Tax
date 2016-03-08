package ch.dvbern.ebegu.api.util.errors;

import org.jboss.resteasy.api.validation.Validation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;

/**
 * Created by imanol on 02.03.16.
 * Basis Exception Mapper
 * @see  <a href="https://samaxes.com/2014/04/jaxrs-beanvalidation-javaee7-wildfly/" >https://samaxes.com/2014/04/jaxrs-beanvalidation-javaee7-wildfly</a>
 */
public abstract class AbstractEbeguExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {

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

}
