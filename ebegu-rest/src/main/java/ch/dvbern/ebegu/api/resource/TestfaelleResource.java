package ch.dvbern.ebegu.api.resource;

import java.time.LocalDate;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.services.SchulungService;
import ch.dvbern.ebegu.services.TestfaelleService;
import ch.dvbern.ebegu.util.DateUtil;
import io.swagger.annotations.Api;

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

	@Inject
	private SchulungService schulungService;

	@GET
	@Path("/testfall/{fallid}/{gesuchsperiodeId}/{betreuungenBestaetigt}/{verfuegen}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getTestFall(
		@PathParam("fallid") String fallid,
		@PathParam("gesuchsperiodeId") String gesuchsperiodeId,
		@PathParam("betreuungenBestaetigt") boolean betreuungenBestaetigt,
		@PathParam("verfuegen") boolean verfuegen) {

		StringBuilder responseString = testfaelleService.createAndSaveTestfaelle(fallid, betreuungenBestaetigt, verfuegen, gesuchsperiodeId);
		return Response.ok(responseString.toString()).build();
	}

	@GET
	@Path("/testfallgs/{fallid}/{gesuchsperiodeId}/{betreuungenBestaetigt}/{verfuegen}/{username}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getTestFallGS(
		@PathParam("fallid") String fallid,
		@PathParam("gesuchsperiodeId") String gesuchsperiodeId,
		@PathParam("betreuungenBestaetigt") boolean betreuungenBestaetigt,
		@PathParam("verfuegen") boolean verfuegen,
		@PathParam("username") String username) {

		StringBuilder responseString = testfaelleService.createAndSaveAsOnlineGesuch(fallid, betreuungenBestaetigt, verfuegen, username, gesuchsperiodeId);
		return Response.ok(responseString.toString()).build();
	}

	@DELETE
	@Path("/testfallgs/{username}")
	@Consumes(MediaType.WILDCARD)
	public Response removeFaelleOfGS(
		@PathParam("username") String username) {

		testfaelleService.removeGesucheOfGS(username);
		return Response.ok().build();
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

	@GET
	@Path("/schulung/reset")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response resetSchulungsdaten() {
		schulungService.resetSchulungsdaten();
		return Response.ok("Schulungsdaten zurückgesetzt").build();
	}

	@DELETE
	@Path("/schulung/delete")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteSchulungsdaten() {
		schulungService.deleteSchulungsdaten();
		return Response.ok("Schulungsdaten gelöscht").build();
	}

	@GET
	@Path("/schulung/create")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createSchulungsdaten() {
		schulungService.createSchulungsdaten();
		return Response.ok("Schulungsdaten erstellt").build();
	}

	@GET
	@Path("/schulung/public/user")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	public Response getSchulungBenutzer() {
		String[] schulungBenutzer = schulungService.getSchulungBenutzer();
		return Response.ok(schulungBenutzer).build();
	}
}
