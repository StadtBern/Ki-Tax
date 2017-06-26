package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.services.DatabaseMigrationService;
import io.swagger.annotations.Api;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

/**
 * Resource zum Ausfuehren von manuellen DB-Migrationen
 */
@Path("dbmigration")
@Stateless
@Api
public class DatabaseMigrationResource {

	@Inject
	private DatabaseMigrationService databaseMigrationService;

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
