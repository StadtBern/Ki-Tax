package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.services.TestfaelleService;
import io.swagger.annotations.Api;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Resource zur Erstellung von (vordefinierten) Testfaellen.
 * Alle Testfaelle erstellen:
 * http://localhost:8080/ebegu/api/v1/testfaelle/testfall/all
 */
@Path("testfaelle")
@Stateless
@Api
public class TestfaelleResource {

	@Inject
	private TestfaelleService testfaelleService;

	@GET
	@Path("/testfall/{fallid}/{betreuungenBestaetigt}/{verfuegen}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getTestFall(
		@PathParam("fallid") String fallid,
		@PathParam("betreuungenBestaetigt") boolean betreuungenBestaetigt,
		@PathParam("verfuegen") boolean verfuegen) {

		StringBuilder responseString = testfaelleService.createAndSaveTestfaelle(fallid, 1, betreuungenBestaetigt, verfuegen);
		return Response.ok(responseString.toString()).build();
	}

	@GET
	@Path("/testfall/{fallid}/{iterationCount}/{betreuungenBestaetigt}/{verfuegen}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getTestFall(
		@PathParam("fallid") String fallid,
		@PathParam("iterationCount") Integer iterationCount,
		@PathParam("betreuungenBestaetigt") boolean betreuungenBestaetigt,
		@PathParam("verfuegen") boolean verfuegen) {

		StringBuilder responseString = testfaelleService.createAndSaveTestfaelle(fallid, iterationCount, betreuungenBestaetigt, verfuegen);
		return Response.ok(responseString.toString()).build();
	}

}
