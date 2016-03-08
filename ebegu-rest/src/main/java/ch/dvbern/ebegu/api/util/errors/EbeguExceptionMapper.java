package ch.dvbern.ebegu.api.util.errors;

import ch.dvbern.ebegu.api.util.validation.EbeguExceptionReport;
import ch.dvbern.ebegu.errors.EbeguException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

/**
 * Created by imanol on 01.03.16.
 * ExceptionMapper fuer EbeguExceptions und Subklassen davon
 */
@Provider
public class EbeguExceptionMapper extends AbstractEbeguExceptionMapper<EbeguException> {


	@Override
	public Response toResponse(EbeguException exception) {
		// wollen wir das hier so handhaben?
		return buildViolationReportResponse(exception, Status.BAD_REQUEST);
	}


	@Override
	protected Response buildViolationReportResponse(EbeguException exception, Response.Status status) {
		return EbeguExceptionReport.buildResponse(status, exception, getLocaleFromHeader(), configuration.getIsDevmode());

	}

}

