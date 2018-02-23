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

package ch.dvbern.ebegu.api.resource;

import java.util.Objects;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.dvbern.ebegu.services.DatabaseMigrationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Resource zum Ausfuehren von manuellen DB-Migrationen
 */
@Path("dbmigration")
@Stateless
@Api(description = "Resource zum Ausfuehren von manuellen DB-Migrationen")
public class DatabaseMigrationResource {

	@Inject
	private DatabaseMigrationService databaseMigrationService;

	@ApiOperation(value = "Führt das Skript mit der übergebenen Nummer durch", response = Void.class)
	@GET
	@Path("/{scriptNr}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	@RolesAllowed(SUPER_ADMIN)
	public Response processScript(@PathParam("scriptNr") String scriptNr) {
		Objects.requireNonNull(scriptNr, "scriptNr muss gesetzt sein");
		databaseMigrationService.processScript(scriptNr);
		return Response.ok().build();
	}
}
