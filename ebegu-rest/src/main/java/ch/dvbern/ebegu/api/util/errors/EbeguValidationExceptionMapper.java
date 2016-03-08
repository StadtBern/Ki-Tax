package ch.dvbern.ebegu.api.util.errors;

import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintDefinitionException;
import javax.validation.GroupDefinitionException;
import javax.validation.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

/**
 * Created by imanol on 01.03.16.
 * Exception Mapper der mit Validation Exceptions von javax umgehen kann. Diese werden zum Beispiel geworfen wenn ein
 * ungueltiger Parameter in einen JAX-RS Aufruf reinkommt   .
 * Der Mapper handhabt auch einige Subklassen von ValidationException
 */
@Provider
public class EbeguValidationExceptionMapper extends AbstractEbeguExceptionMapper<ValidationException> {

	private final Logger LOG = LoggerFactory.getLogger(EbeguValidationExceptionMapper.class.getSimpleName());

	@Override
	public Response toResponse(ValidationException exception) {
		LOG.error("ResteasyValidationException occured ", exception);

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
			} else if (resteasyViolationException.getReturnValueViolations().isEmpty()) {
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
