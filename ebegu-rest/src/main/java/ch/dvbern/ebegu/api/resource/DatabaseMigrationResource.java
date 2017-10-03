package ch.dvbern.ebegu.api.resource;

import java.util.Objects;

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
	public Response processScript(@PathParam("scriptNr") String scriptNr) {
		Objects.requireNonNull(scriptNr, "scriptNr muss gesetzt sein");
		databaseMigrationService.processScript(scriptNr);
		return Response.ok().build();
	}
}
