package ch.dvbern.ebegu.api.errors;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJBTransactionRolledbackException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

/**
 * Created by imanol on 01.03.16.
 * ExceptionMapper fuer EbeguExceptions und Subklassen davon
 */
@Provider
public class EbeguConstraintValidationExceptionMapper extends AbstractEbeguExceptionMapper<EJBTransactionRolledbackException> {

	private final Logger LOG = LoggerFactory.getLogger(EbeguConstraintValidationExceptionMapper.class.getSimpleName());
	@Override
	protected Response buildViolationReportResponse(EJBTransactionRolledbackException exception, Status status) {
		return null;
	}

	@Override
	public Response toResponse(EJBTransactionRolledbackException exception) {
		LOG.warn("Constraint Violation occured ", exception);
		Throwable rootCause = ExceptionUtils.getRootCause(exception);
		if (rootCause instanceof ConstraintViolationException) {
			ConstraintViolationException constViolationEx = (ConstraintViolationException) rootCause;
			ResteasyViolationException resteasyViolationException = new ResteasyViolationException(constViolationEx.getConstraintViolations());
			resteasyViolationException.getAccept().add(MediaType.APPLICATION_JSON_TYPE);
			return ViolationReportCreator.
				buildViolationReportResponse(resteasyViolationException, Status.INTERNAL_SERVER_ERROR, getAcceptMediaType(resteasyViolationException.getAccept()));
		}

		return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
	}

}

