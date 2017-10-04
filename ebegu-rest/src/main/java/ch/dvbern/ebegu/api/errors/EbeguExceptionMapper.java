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

import ch.dvbern.ebegu.api.validation.EbeguExceptionReport;
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

