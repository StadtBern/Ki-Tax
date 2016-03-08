package ch.dvbern.ebegu.api.util.errors;

import ch.dvbern.ebegu.api.util.validation.EbeguExceptionReport;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
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
		logException(exception);
		//standardfall, wenn manche subexceptions speziell gehandhabt werden muessen kann mit instanceof ein if block gemacht werden

		if (exception instanceof EbeguEntityNotFoundException) {
			// wollen wir das hier so handhaben?
			EbeguEntityNotFoundException ebeguEntityNotFoundException = EbeguEntityNotFoundException.class.cast(exception);
			return buildViolationReportResponse(ebeguEntityNotFoundException, Status.NOT_FOUND);
		}
		return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);

	}


	@Override
	protected Response buildViolationReportResponse(EbeguRuntimeException exception, Response.Status status) {

		return EbeguExceptionReport.buildResponse(status, exception, getLocaleFromHeader());

	}



}
