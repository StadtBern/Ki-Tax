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

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxInstitution;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.InstitutionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

/**
 * REST Resource fuer Institution
 */
@Path("institutionen")
@Stateless
@Api(description = "Resource für Institutionen (Anbieter eines Betreuungsangebotes)")
public class InstitutionResource {

	@Inject
	private InstitutionService institutionService;

	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Creates a new Institution in the database.", response = JaxInstitution.class)
	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createInstitution(
		@Nonnull @NotNull JaxInstitution institutionJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Institution convertedInstitution = converter.institutionToEntity(institutionJAXP, new Institution());
		Institution persistedInstitution = this.institutionService.createInstitution(convertedInstitution);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(InstitutionResource.class)
			.path('/' + persistedInstitution.getId())
			.build();

		JaxInstitution jaxInstitution = converter.institutionToJAX(persistedInstitution);
		return Response.created(uri).entity(jaxInstitution).build();
	}

	@ApiOperation(value = "Update a Institution in the database.", response = JaxInstitution.class)
	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxInstitution updateInstitution(
		@Nonnull @NotNull @Valid JaxInstitution institutionJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(institutionJAXP.getId());
		Optional<Institution> optInstitution = institutionService.findInstitution(institutionJAXP.getId());
		Institution institutionFromDB = optInstitution.orElseThrow(() -> new EbeguEntityNotFoundException("update", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionJAXP.getId()));

		Institution institutionToMerge = converter.institutionToEntity(institutionJAXP, institutionFromDB);
		Institution modifiedInstitution = this.institutionService.updateInstitution(institutionToMerge);
		return converter.institutionToJAX(modifiedInstitution);
	}

	@ApiOperation(value = "Find and return an Institution by his institution id as parameter",
		response = JaxInstitution.class)
	@Nullable
	@GET
	@Path("/id/{institutionId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxInstitution findInstitution(
		@Nonnull @NotNull @PathParam("institutionId") JaxId institutionJAXPId) {

		Validate.notNull(institutionJAXPId.getId());
		String institutionID = converter.toEntityId(institutionJAXPId);
		Optional<Institution> optional = institutionService.findInstitution(institutionID);

		return optional.map(institution -> converter.institutionToJAX(institution)).orElse(null);
	}

	@ApiOperation("Remove an Institution logically by his institution-id as parameter")
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@Nullable
	@DELETE
	@Path("/{institutionId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeInstitution(
		@Nonnull @NotNull @PathParam("institutionId") JaxId institutionJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(institutionJAXPId.getId());
		institutionService.setInstitutionInactive(converter.toEntityId(institutionJAXPId));
		return Response.ok().build();
	}

	@ApiOperation(value = "Find and return a list of Institution by the Traegerschaft as parameter",
		responseContainer = "List", response = JaxInstitution.class)
	@Nonnull
	@GET
	@Path("/traegerschaft/{traegerschaftId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxInstitution> getAllInstitutionenFromTraegerschaft(
		@Nonnull @NotNull @PathParam("traegerschaftId") JaxId traegerschaftJAXPId) {

		Validate.notNull(traegerschaftJAXPId.getId());
		String traegerschaftId = converter.toEntityId(traegerschaftJAXPId);
		return institutionService.getAllInstitutionenFromTraegerschaft(traegerschaftId).stream()
			.map(institution -> converter.institutionToJAX(institution))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Find and return a list of all Institutionen",
		responseContainer = "List", response = JaxInstitution.class)
	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxInstitution> getAllInstitutionen() {
		return institutionService.getAllInstitutionen().stream()
			.map(inst -> converter.institutionToJAX(inst))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Find and return a list of all active Institutionen. An active Institution is a Institution " +
		"where the active flag is true", responseContainer = "List", response = JaxInstitution.class)
	@Nonnull
	@GET
	@Path("/active")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxInstitution> getAllActiveInstitutionen() {
		return institutionService.getAllActiveInstitutionen().stream()
			.map(inst -> converter.institutionToJAX(inst))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Find and return a list of all Institutionen of the currently logged in Benutzer. Retruns " +
		"all for admins", responseContainer = "List", response = JaxInstitution.class)
	@Nonnull
	@GET
	@Path("/currentuser")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxInstitution> getAllowedInstitutionenForCurrentBenutzer() {
		return institutionService.getAllowedInstitutionenForCurrentBenutzer(true).stream()
			.map(inst -> converter.institutionToJAX(inst))
			.collect(Collectors.toList());
	}
}
