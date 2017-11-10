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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBAccessException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dvbern.ebegu.api.util.RestUtil;

/**
 * Created by imanol on 01.03.16.
 * ExceptionMapper fuer EJBTransactionRolledbackException. Dient zum Beispiel dazu ConstraintViolationException abzufangen
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
		Throwable rootCause = ExceptionUtils.getRootCause(exception);
		if (rootCause instanceof ConstraintViolationException) {
			LOG.warn("Constraint Violation occured ", exception);
			ConstraintViolationException constViolationEx = (ConstraintViolationException) rootCause;
			ResteasyViolationException resteasyViolationException = new ResteasyViolationException(constViolationEx.getConstraintViolations());
			List<MediaType> acceptedTypes = new ArrayList<>(resteasyViolationException.getAccept());
			acceptedTypes.add(MediaType.APPLICATION_JSON_TYPE);
			return ViolationReportCreator.
				buildViolationReportResponse(resteasyViolationException, Status.CONFLICT, getAcceptMediaType(acceptedTypes));
		}
		if (rootCause instanceof EJBAccessException) {
			return RestUtil.sendErrorNotAuthorized();    // nackte 403 status antwort
		}
		// wir bauen hier auch eine eigene response fuer EJBTransactionRolledbackException die wir nicht erwarten
		// die unwrapped exception sollten wir nur zurueckgeben wenn wir im dev mode sind um keine infos zu leaken
		if (configuration.getIsDevmode()) {
			return buildResponse(unwrapException(exception), MediaType.TEXT_PLAIN, Status.INTERNAL_SERVER_ERROR);
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Internal error in E-Begu. Timestamp: " +
				LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)).type("text/plain").build();
		}
	}
}

