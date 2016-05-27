package ch.dvbern.ebegu.api.errors;

import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by imanol on 25.05.16.
 */
public class ViolationReportCreator {

	public static Response buildViolationReportResponse(ResteasyViolationException exception, Response.Status status, MediaType acceptedMedia) {
		Response.ResponseBuilder builder = Response.status(status);
		builder.header(Validation.VALIDATION_HEADER, "true");

		// Check standard media types.
		if (acceptedMedia != null) {
			builder.type(acceptedMedia);
			builder.entity(new ViolationReport(exception));
			return builder.build();
		}

		// Default media type.
		builder.type(MediaType.TEXT_PLAIN);
		builder.entity(exception.toString());
		return builder.build();
	}

}
