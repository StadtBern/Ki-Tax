/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

package ch.dvbern.ebegu.api.resource;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.DailyBatch;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource fuer DailyBatch. Dies darf nur als SUPERADMIN aufgerufen werden
 */
@Path("dailybatch")
@Stateless
@Api(description = "Resource für die DailyBatch Jobs")
@RolesAllowed({ UserRoleName.SUPER_ADMIN })
public class DailyBatchResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(DailyBatchResource.class);

	@Inject
	private DailyBatch dailyBatch;

	@ApiOperation(value = "Führt den Job runBatchMahnungFristablauf aus.", response = JaxGesuch.class)
	@Nullable
	@GET
	@Path("/mahnungFristAblauf")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response runBatchMahnungFristablauf() throws EbeguException {
		Future<Boolean> booleanFuture = dailyBatch.runBatchMahnungFristablauf();
		try {
			Boolean resultat = booleanFuture.get();
			LOGGER.info("Manuelle ausführung! Batchjob MahnungFristablauf durchgefuehrt mit Resultat: {}", resultat);
			return Response.ok().build();
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Manuelle ausführung! Batch-Job Mahnung Fristablauf konnte nicht durchgefuehrt werden!", e);
			return Response.serverError().build();
		}
	}
}
