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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;

/**
 * Created by imanol on 25.05.16.
 * Helper to create a ViolationReport Object from a ResteasyViolationException. Returns the created Report in the Response
 */
public final class ViolationReportCreator {

	private ViolationReportCreator() {
	}

	public static Response buildViolationReportResponse(ResteasyViolationException exception, Status status, @Nullable MediaType acceptedMedia) {
		Response.ResponseBuilder builder = Response.status(status);
		builder.header(Validation.VALIDATION_HEADER, "true");

		// homa: not sure if it makes sense to even check this but our client should always ask for a specific media type (namely application/json)
		// Check standard media types.
		if (acceptedMedia != null) {
			return constructResponse(exception, builder, acceptedMedia);
		}
		// Default media type. Es muss JSON sein, da der Client sonst mit der Nachricht nicht umgehen kann und die Fehlermeldung als
		// unerwarteter Fehler angezeit wird
		return constructResponse(exception, builder, MediaType.APPLICATION_JSON_TYPE);
	}

	/**
	 * Setzt den Typ und die Exception als ValidationReport. acceptedMedia sollte deshalb ein Typ sein, der ein Objekt
	 * als entity akzeptiert: JSON, XML
	 */
	private static Response constructResponse(ResteasyViolationException exception, ResponseBuilder builder, @Nonnull MediaType acceptedMedia) {
		builder.type(acceptedMedia);
		builder.entity(new ViolationReport(exception));
		return builder.build();
	}

}
