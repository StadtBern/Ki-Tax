/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.api.errors;

import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintDefinitionException;
import javax.validation.GroupDefinitionException;
import javax.validation.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
				return ViolationReportCreator.buildViolationReportResponse(resteasyViolationException, Status.BAD_REQUEST, getAcceptMediaType(resteasyViolationException.getAccept()));
			} else {
				return ViolationReportCreator.buildViolationReportResponse(resteasyViolationException, Status.INTERNAL_SERVER_ERROR, getAcceptMediaType(resteasyViolationException.getAccept()));
			}
		}
		return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
	}

	@Override
	protected Response buildViolationReportResponse(ValidationException exception, Status status) {
		return null;
	}
}
