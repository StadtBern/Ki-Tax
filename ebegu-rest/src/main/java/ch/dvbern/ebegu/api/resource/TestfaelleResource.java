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

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.services.SchulungService;
import ch.dvbern.ebegu.services.TestfaelleService;
import ch.dvbern.ebegu.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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
@Api(description = "Resource zur Erstellung von (vordefinierten) Testfaellen")
public class TestfaelleResource {

	private static final String FALL = "Fall ";

	@Inject
	private TestfaelleService testfaelleService;

	@Inject
	private SchulungService schulungService;

	@ApiOperation(value = "Erstellt einen Testfall aus mehreren vordefinierten Testfaellen. Folgende Einstellungen " +
		"sind moeglich: Gesuchsperiode, Status der Betreuungen, Gesuch verfuegen", response = String.class)
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

	@ApiOperation(value = "Erstellt einen Testfall aus mehreren vordefinierten Testfaellen fuer einen Gesuchsteller " +
		"(Online Gesuch). Folgende Einstellungen sind moeglich: Gesuchsperiode, Status der Betreuungen, Gesuch " +
		"verfuegen, gewuenschter Gesuchsteller", response = String.class)
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

	@ApiOperation(value = "Loescht alle Antraege des uebergebenen Gesuchstellers.", response = String.class)
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@DELETE
	@Path("/testfallgs/{username}")
	@Consumes(MediaType.WILDCARD)
	public Response removeFaelleOfGS(
		@PathParam("username") String username) {

		testfaelleService.removeGesucheOfGS(username);
		return Response.ok().build();
	}

	@ApiOperation(value = "Simuliert fuer den uebergebenen Testfall eine Heirat", response = String.class)
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

	@ApiOperation(value = "Simuliert fuer den uebergebenen Testfall eine Scheidung", response = String.class)
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

	@ApiOperation(value = "Setzt die Schulungsdaten zurueck", response = String.class)
	@GET
	@Path("/schulung/reset")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response resetSchulungsdaten() {
		schulungService.resetSchulungsdaten();
		return Response.ok("Schulungsdaten zurückgesetzt").build();
	}

	@ApiOperation(value = "Loescht alle in der Schulung erstellten Daten.", response = String.class)
	@DELETE
	@Path("/schulung/delete")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteSchulungsdaten() {
		schulungService.deleteSchulungsdaten();
		return Response.ok("Schulungsdaten gelöscht").build();
	}

	@ApiOperation(value = "Erstellt die Schulungsdaten", response = String.class)
	@GET
	@Path("/schulung/create")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createSchulungsdaten() {
		schulungService.createSchulungsdaten();
		return Response.ok("Schulungsdaten erstellt").build();
	}

	@ApiOperation(value = "Gibt eine Liste der Schulungsbenutzer zurueck",
		responseContainer = "Array", response = String.class)
	@GET
	@Path("/schulung/public/user")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	public Response getSchulungBenutzer() {
		String[] schulungBenutzer = schulungService.getSchulungBenutzer();
		return Response.ok(schulungBenutzer).build();
	}
}
