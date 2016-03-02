package ch.dvbern.ebegu.api.util.errors;

import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintDefinitionException;
import javax.validation.GroupDefinitionException;
import javax.validation.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;

/**
 * Created by imanol on 01.03.16.
 */
@Provider
public class EbeguValidationExceptionMapper extends AbstractEbeguExceptionMapper<ValidationException> {

	@Override
	public Response toResponse(ValidationException exception) {
		if (exception instanceof ConstraintDefinitionException) {
			return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
		}
		if (exception instanceof ConstraintDeclarationException) {
			return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
		}
		if (exception instanceof GroupDefinitionException) {
			return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
		}
		if (exception instanceof ResteasyViolationException) {
			ResteasyViolationException resteasyViolationException = ResteasyViolationException.class.cast(exception);
			Exception e = resteasyViolationException.getException();
			if (e != null) {
				return buildResponse(unwrapException(e), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
			} else if (resteasyViolationException.getReturnValueViolations().size() == 0) {
				return buildViolationReportResponse(resteasyViolationException, Status.BAD_REQUEST);
			} else {
				return buildViolationReportResponse(resteasyViolationException, Status.INTERNAL_SERVER_ERROR);
			}
		}
		return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
	}


	protected Response buildViolationReportResponse(ResteasyViolationException exception, Status status) {
		ResponseBuilder builder = Response.status(status);
		builder.header(Validation.VALIDATION_HEADER, "true");

		// Check standard media types.
		MediaType mediaType = getAcceptMediaType(exception.getAccept());
		if (mediaType != null) {
			builder.type(mediaType);
			builder.entity(new ViolationReport(exception));
			return builder.build();
		}

		// Default media type.
		builder.type(MediaType.TEXT_PLAIN);
		builder.entity(exception.toString());
		return builder.build();
	}

	@Override
	protected Response buildViolationReportResponse(ValidationException exception, Status status) {
		return null;
	}
}
