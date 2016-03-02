package ch.dvbern.ebegu.api.util.errors;

import ch.dvbern.ebegu.api.util.validation.EbeguExceptionReport;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import org.jboss.resteasy.api.validation.Validation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

/**
 * Created by imanol on 01.03.16.
 */
@Provider
public class EbeguExceptionMapper extends AbstractEbeguExceptionMapper<EbeguException> {

	@Override
	public Response toResponse(EbeguException exception) {
		if (exception instanceof EbeguEntityNotFoundException) {
			EbeguEntityNotFoundException ebeguEntityNotFoundException = EbeguEntityNotFoundException.class.cast(exception);
			return buildViolationReportResponse(ebeguEntityNotFoundException, Status.BAD_REQUEST);
		}
		return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
	}

	// todo Diese Methode ist gleich wie in EbeguRuntimeExceptionMapper.
	// Wenn wir keine unterschiedliche Implemetierung brauchen sollten wir nur eine haben.
	// Das hängt aber von der implementierung ab.
	protected Response buildViolationReportResponse(EbeguException exception, Response.Status status) {
		Response.ResponseBuilder builder = Response.status(status);
		builder.header(Validation.VALIDATION_HEADER, "true");

		// todo gibt immer JASON zurueck. man sollte zuerst schauen welche Mime_Types erlaubt sind ???
		builder.type(MediaType.APPLICATION_JSON_TYPE);
		builder.entity(new EbeguExceptionReport(exception));
		return builder.build();

	}

}

