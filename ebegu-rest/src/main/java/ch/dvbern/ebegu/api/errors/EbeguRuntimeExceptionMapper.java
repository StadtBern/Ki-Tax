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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import ch.dvbern.ebegu.api.validation.EbeguExceptionReport;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguExistingAntragException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;

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

		if (exception instanceof EbeguExistingAntragException) {
			// wollen wir das hier so handhaben?
			EbeguExistingAntragException ebeguExistingAntragException = EbeguExistingAntragException.class.cast(exception);
			return buildViolationReportResponse(ebeguExistingAntragException, Status.NOT_FOUND);
		}
		if (exception instanceof EbeguEntityNotFoundException) {
			// wollen wir das hier so handhaben?
			EbeguEntityNotFoundException ebeguEntityNotFoundException = EbeguEntityNotFoundException.class.cast(exception);
			return buildViolationReportResponse(ebeguEntityNotFoundException, Status.NOT_FOUND);
		}
		return buildViolationReportResponse(exception, Status.INTERNAL_SERVER_ERROR);

	}

	@Override
	protected Response buildViolationReportResponse(EbeguRuntimeException exception, Response.Status status) {

		return EbeguExceptionReport.buildResponse(status, exception, getLocaleFromHeader(), configuration.getIsDevmode());

	}

}
