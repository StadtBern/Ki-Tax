package ch.dvbern.ebegu.api.util.errors;

import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.api.util.validation.EbeguExceptionReport;
import org.jboss.resteasy.api.validation.Validation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

/**
 * Created by imanol on 02.03.16.
 */
@Provider
public class EbeguRuntimeExceptionMapper extends AbstractEbeguExceptionMapper<EbeguRuntimeException> {

	@Override
	public Response toResponse(EbeguRuntimeException exception) {
		if (exception instanceof EbeguRuntimeException) {
			EbeguRuntimeException ebeguRuntimeException = EbeguRuntimeException.class.cast(exception);
			return buildViolationReportResponse(ebeguRuntimeException, Status.BAD_REQUEST);
		}
		return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
	}


	protected Response buildViolationReportResponse(EbeguRuntimeException exception, Response.Status status) {
		Response.ResponseBuilder builder = Response.status(status);
		builder.header(Validation.VALIDATION_HEADER, "true");

		// todo gibt immer JASON zurueck. man sollte zuerst schauen welche Mime_Types erlaubt sind ???
		builder.type(MediaType.APPLICATION_JSON_TYPE);
		builder.entity(new EbeguExceptionReport(exception.getEbeguException()));
		return builder.build();

	}

}
