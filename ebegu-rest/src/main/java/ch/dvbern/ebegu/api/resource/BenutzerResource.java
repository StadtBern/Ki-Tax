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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAuthLoginElement;
import ch.dvbern.ebegu.api.dtos.JaxBenutzerSearchresultDTO;
import ch.dvbern.ebegu.api.dtos.JaxBerechtigungHistory;
import ch.dvbern.ebegu.dto.suchfilter.smarttable.BenutzerTableFilterDTO;
import ch.dvbern.ebegu.dto.suchfilter.smarttable.PaginationDTO;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.util.MonitoringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.JURIST;
import static ch.dvbern.ebegu.enums.UserRoleName.REVISOR;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_INSTITUTION;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT;
import static ch.dvbern.ebegu.enums.UserRoleName.SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.STEUERAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * REST Resource fuer Benutzer  (Auf client userRS.rest.ts also eigentlich die UserResources)
 */
@Path("benutzer")
@Stateless
@Api(description = "Resource für die Verwaltung der Benutzer (User)")
public class BenutzerResource {

	@Inject
	private BenutzerService benutzerService;

	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Gibt alle existierenden Benutzer mit Rolle ADMIN oder SACHBEARBEITER_JA zurueck", responseContainer = "List", response = JaxAuthLoginElement.class)
	@Nonnull
	@GET
	@Path("/JAorAdmin")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, JURIST, REVISOR, STEUERAMT, SCHULAMT , ADMINISTRATOR_SCHULAMT})
	public List<JaxAuthLoginElement> getBenutzerJAorAdmin() {
		return benutzerService.getBenutzerJAorAdmin().stream()
			.map(benutzer -> converter.benutzerToAuthLoginElement(benutzer))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Gibt alle existierenden Benutzer mit Rolle ADMINISTRATOR_SCHULAMT oder SCHULAMT zurueck", responseContainer = "List", response = JaxAuthLoginElement.class)
	@Nonnull
	@GET
	@Path("/SCHorAdmin")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, JURIST, REVISOR, STEUERAMT, SCHULAMT , ADMINISTRATOR_SCHULAMT})
	public List<JaxAuthLoginElement> getBenutzerSCHorAdminSCH() {
		return benutzerService.getBenutzerSCHorAdminSCH().stream()
			.map(benutzer -> converter.benutzerToAuthLoginElement(benutzer))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Gibt alle existierenden Benutzer mit Rolle Gesuchsteller zurueck", responseContainer = "List", response = JaxAuthLoginElement.class)
	@Nonnull
	@GET
	@Path("/gesuchsteller")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(SUPER_ADMIN)
	public List<JaxAuthLoginElement> getGesuchsteller() {
		return benutzerService.getGesuchsteller().stream()
			.map(benutzer -> converter.benutzerToAuthLoginElement(benutzer))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Sucht Benutzer mit den uebergebenen Suchkriterien/Filtern.", response = JaxBenutzerSearchresultDTO.class)
	@Nonnull
	@POST
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ SUPER_ADMIN, ADMIN, ADMINISTRATOR_SCHULAMT})
	public Response searchBenutzer(
		@Nonnull @NotNull BenutzerTableFilterDTO benutzerSearch,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		return MonitoringUtil.monitor(GesuchResource.class, "searchBenutzer", () -> {
			Pair<Long, List<Benutzer>> searchResultPair = benutzerService.searchBenutzer(benutzerSearch);
			List<Benutzer> foundBenutzer = searchResultPair.getRight();

			List<JaxAuthLoginElement> benutzerDTOList = new ArrayList<>();
			foundBenutzer.forEach(benutzer -> {
				JaxAuthLoginElement benutzerDTO = converter.benutzerToAuthLoginElement(benutzer);
				benutzerDTOList.add(benutzerDTO);
			});

			JaxBenutzerSearchresultDTO resultDTO = buildResultDTO(benutzerSearch, searchResultPair, benutzerDTOList);
			return Response.ok(resultDTO).build();
		});
	}

	@Nonnull
	private JaxBenutzerSearchresultDTO buildResultDTO(@Nonnull @NotNull BenutzerTableFilterDTO benutzerSearch, Pair<Long, List<Benutzer>> searchResultPair,
		List<JaxAuthLoginElement> benutzerDTOList) {

		JaxBenutzerSearchresultDTO resultDTO = new JaxBenutzerSearchresultDTO();
		resultDTO.setBenutzerDTOs(benutzerDTOList);
		PaginationDTO pagination = benutzerSearch.getPagination();
		pagination.setTotalItemCount(searchResultPair.getLeft());
		resultDTO.setPaginationDTO(pagination);
		return resultDTO;
	}

	@ApiOperation(value = "Sucht den Benutzer mit dem uebergebenen Username in der Datenbank.",
		response = JaxAuthLoginElement.class)
	@Nullable
	@GET
	@Path("/username/{username}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ SUPER_ADMIN, ADMIN, ADMINISTRATOR_SCHULAMT})
	public JaxAuthLoginElement findBenutzer(
		@Nonnull @NotNull @PathParam("username") String username) {

		Validate.notNull(username);
		Optional<Benutzer> benutzerOptional = benutzerService.findBenutzer(username);

		return benutzerOptional
			.map(benutzer -> converter.benutzerToAuthLoginElement(benutzer))
			.orElse(null);
	}

	@ApiOperation(value = "Inactivates a Benutzer in the database", response = JaxAuthLoginElement.class)
	@Nullable
	@PUT
	@Path("/inactivate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ SUPER_ADMIN, ADMIN, ADMINISTRATOR_SCHULAMT})
	public JaxAuthLoginElement inactivateBenutzer(
		@Nonnull @NotNull @Valid JaxAuthLoginElement benutzerJax, @Context UriInfo uriInfo, @Context HttpServletResponse response) {

		Benutzer benutzer = benutzerService.sperren(benutzerJax.getUsername());
		return converter.benutzerToAuthLoginElement(benutzer);
	}

	@ApiOperation(value = "Reactivates a Benutzer in the database", response = JaxAuthLoginElement.class)
	@Nullable
	@PUT
	@Path("/reactivate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ SUPER_ADMIN, ADMIN, ADMINISTRATOR_SCHULAMT})
	public JaxAuthLoginElement reactivateBenutzer(
		@Nonnull @NotNull @Valid JaxAuthLoginElement benutzerJax, @Context UriInfo uriInfo, @Context HttpServletResponse response) {

		Benutzer benutzer = benutzerService.reaktivieren(benutzerJax.getUsername());
		return converter.benutzerToAuthLoginElement(benutzer);
	}

	@ApiOperation(value = "Updates a Benutzer in the database", response = JaxAuthLoginElement.class)
	@Nullable
	@PUT
	@Path("/saveBenutzerBerechtigungen")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ SUPER_ADMIN, ADMIN, ADMINISTRATOR_SCHULAMT})
	public JaxAuthLoginElement saveBenutzerBerechtigungen(
		@Nonnull @NotNull @Valid JaxAuthLoginElement benutzerJax,
		@Context UriInfo uriInfo, @Context HttpServletResponse response) {

		String username = benutzerJax.getUsername();
		Benutzer benutzer = benutzerService.findBenutzer(username)
			.orElseThrow(() -> new EbeguEntityNotFoundException("saveBenutzerBerechtigungen", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, username));

		boolean currentBerechtigungChanged = hasCurrentBerechtigungChanged(benutzerJax, benutzer);
		Benutzer mergedBenutzer = benutzerService.saveBenutzerBerechtigungen(converter.authLoginElementToBenutzer(benutzerJax, benutzer), currentBerechtigungChanged);

		return converter.benutzerToAuthLoginElement(mergedBenutzer);
	}

	private boolean hasCurrentBerechtigungChanged(@Nonnull JaxAuthLoginElement jaxBenutzerNew, @Nonnull Benutzer benutzerOld) {
		JaxAuthLoginElement jaxBenutzerOld = converter.benutzerToAuthLoginElement(benutzerOld);
		jaxBenutzerOld.evaluateCurrentBerechtigung();
		jaxBenutzerNew.evaluateCurrentBerechtigung();
		Objects.requireNonNull(jaxBenutzerOld.getCurrentBerechtigung());
		Objects.requireNonNull(jaxBenutzerNew.getCurrentBerechtigung());
		return !jaxBenutzerOld.getCurrentBerechtigung().isSame(jaxBenutzerNew.getCurrentBerechtigung());
	}


	@ApiOperation(value = "Gibt alle BerechtigungHistory Einträge des übergebenen Benutzers zurück",
		responseContainer = "List", response = JaxBerechtigungHistory.class)
	@Nonnull
	@GET
	@Path("/berechtigunghistory/{username}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({SUPER_ADMIN, ADMIN, ADMINISTRATOR_SCHULAMT})
	public List<JaxBerechtigungHistory> getBerechtigungHistoriesForBenutzer(@Nonnull @NotNull @PathParam("username") String username) {
		Benutzer benutzer = benutzerService.findBenutzer(username).orElseThrow(()
			-> new EbeguEntityNotFoundException("getBerechtigungHistoriesForBenutzer", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "username invalid: " + username));
		return benutzerService.getBerechtigungHistoriesForBenutzer(benutzer).stream()
			.map(history -> converter.berechtigungHistoryToJax(history))
			.collect(Collectors.toList());
	}
}
