package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.services.TestfaelleService;
import ch.dvbern.ebegu.util.DateUtil;
import io.swagger.annotations.Api;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;

/**
 * REST Resource zur Erstellung von (vordefinierten) Testfaellen.
 * Alle Testfaelle erstellen:
 * http://localhost:8080/ebegu/api/v1/testfaelle/testfall/all
 */
@Path("testfaelle")
@Stateless
@Api
public class TestfaelleResource {

	private static final String FALL = "Fall ";

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
	@Path("/testfallgs/{fallid}/{betreuungenBestaetigt}/{verfuegen}/{username}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getTestFallGS(
		@PathParam("fallid") String fallid,
		@PathParam("betreuungenBestaetigt") boolean betreuungenBestaetigt,
		@PathParam("verfuegen") boolean verfuegen,
		@PathParam("username") String username) {

		StringBuilder responseString = testfaelleService.createAndSaveAsOnlineGesuch(fallid, betreuungenBestaetigt, verfuegen, username);
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

	@GET
	@Path("/mutationHeirat/{fallNummer}/{gesuchsperiodeid}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response mutationHeirat(
		@PathParam("fallNummer") Long fallNummer,
		@PathParam("gesuchsperiodeid") String gesuchsperiodeid,
		@Nullable @QueryParam("mutationsdatum") String stringMutationsdatum,
		@Nullable @QueryParam("aenderungper") String stringAenderungPer) {

		LocalDate mutationsdatum = DateUtil.parseStringToDateOrReturnNow(stringMutationsdatum);
		LocalDate aenderungPer = DateUtil.parseStringToDateOrReturnNow(stringAenderungPer);

		final Gesuch gesuch = testfaelleService.mutierenHeirat(fallNummer, gesuchsperiodeid, mutationsdatum, aenderungPer, false);
		if (gesuch != null) {
			return Response.ok(FALL + gesuch.getFall().getFallNummer() + " mutiert zu heirat").build();
		}

		return Response.ok(FALL + fallNummer + " konnte nicht mutiert").build();
	}

	@GET
	@Path("/mutationScheidung/{fallNummer}/{gesuchsperiodeid}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response mutierenScheidung(
		@PathParam("fallNummer") Long fallNummer,
		@PathParam("gesuchsperiodeid") String gesuchsperiodeid,
		@Nullable @QueryParam("mutationsdatum") String stringMutationsdatum,
		@Nullable @QueryParam("aenderungper") String stringAenderungPer) {

		LocalDate mutationsdatum = DateUtil.parseStringToDateOrReturnNow(stringMutationsdatum);
		LocalDate aenderungPer = DateUtil.parseStringToDateOrReturnNow(stringAenderungPer);

		final Gesuch gesuch = testfaelleService.mutierenScheidung(fallNummer, gesuchsperiodeid, mutationsdatum, aenderungPer, false);
		if (gesuch != null) {
			return Response.ok(FALL + gesuch.getFall().getFallNummer() + " mutiert zu scheidung").build();
		}

		return Response.ok(FALL + fallNummer + " konnte nicht mutiert").build();
	}

}
