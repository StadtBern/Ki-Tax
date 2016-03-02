package ch.dvbern.ebegu.api.util.errors;

import org.jboss.resteasy.api.validation.Validation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Iterator;
import java.util.List;

/**
 * Created by imanol on 02.03.16.
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

	protected MediaType getAcceptMediaType(List<MediaType> accept) {
		for (MediaType mt : accept) {
            /*
             * application/xml media type causes an exception:
             * org.jboss.resteasy.core.NoMessageBodyWriterFoundFailure: Could not find MessageBodyWriter for response
             * object of type: org.jboss.resteasy.api.validation.ViolationReport of media type: application/xml
             * Not anymore
             */
			if (MediaType.APPLICATION_XML_TYPE.getType().equals(mt.getType())
				&& MediaType.APPLICATION_XML_TYPE.getSubtype().equals(mt.getSubtype())) {
				return MediaType.APPLICATION_XML_TYPE;
			}
			if (MediaType.APPLICATION_JSON_TYPE.getType().equals(mt.getType())
				&& MediaType.APPLICATION_JSON_TYPE.getSubtype().equals(mt.getSubtype())) {
				return MediaType.APPLICATION_JSON_TYPE;
			}
		}
		return null;
	}

}
