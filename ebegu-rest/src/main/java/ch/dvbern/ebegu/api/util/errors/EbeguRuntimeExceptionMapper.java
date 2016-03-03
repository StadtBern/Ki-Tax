package ch.dvbern.ebegu.api.util.errors;

import ch.dvbern.ebegu.api.util.validation.EbeguExceptionReport;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

/**
 * Created by imanol on 02.03.16.
 * Exception Mapper fuer Runtime Exceptions
 */
@Provider
public class EbeguRuntimeExceptionMapper extends AbstractEbeguExceptionMapper<EbeguRuntimeException> {

	@Override
	public Response toResponse(EbeguRuntimeException exception) {
		//wie handhaben wir die subexceptions
		if (exception instanceof EbeguRuntimeException) {
			EbeguRuntimeException ebeguRuntimeException = EbeguRuntimeException.class.cast(exception);
			return buildViolationReportResponse(ebeguRuntimeException, Status.BAD_REQUEST);
		}
		return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
	}


	protected Response buildViolationReportResponse(EbeguRuntimeException exception, Response.Status status) {
		return EbeguExceptionReport.buildResponse(status, exception);

	}

}
