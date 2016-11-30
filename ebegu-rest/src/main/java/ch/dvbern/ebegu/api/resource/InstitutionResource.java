package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxInstitution;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.InstitutionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Resource fuer Institution
 */
@Path("institutionen")
@Stateless
@Api
public class InstitutionResource {

	@Inject
	private InstitutionService institutionService;

	@Inject
	private JaxBConverter converter;

	@ApiOperation(value = "Creates a new Institution in the database.")
	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createInstitution(
		@Nonnull @NotNull JaxInstitution institutionJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Institution convertedInstitution = converter.institutionToEntity(institutionJAXP, new Institution());
		Institution persistedInstitution = this.institutionService.createInstitution(convertedInstitution);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(InstitutionResource.class)
			.path("/" + persistedInstitution.getId())
			.build();

		JaxInstitution jaxInstitution = converter.institutionToJAX(persistedInstitution);

		return Response.created(uri).entity(jaxInstitution).build();
	}

	@ApiOperation(value = "Update a Institution in the database.")
	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxInstitution updateInstitution(
		@Nonnull @NotNull @Valid JaxInstitution institutionJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(institutionJAXP.getId());
		Optional<Institution> optInstitution = institutionService.findInstitution(institutionJAXP.getId());
		Institution institutionFromDB = optInstitution.orElseThrow(() -> new EbeguEntityNotFoundException("update", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionJAXP.getId()));

		Institution institutionToMerge = converter.institutionToEntity(institutionJAXP, institutionFromDB);
		Institution modifiedInstitution = this.institutionService.updateInstitution(institutionToMerge);

		return converter.institutionToJAX(modifiedInstitution);
	}

	@ApiOperation(value = "Find and return an Institution by his institution id as parameter")
	@Nullable
	@GET
	@Path("/{institutionId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxInstitution findInstitution(
		@Nonnull @NotNull @PathParam("institutionId") JaxId institutionJAXPId) throws EbeguException {

		Validate.notNull(institutionJAXPId.getId());
		String institutionID = converter.toEntityId(institutionJAXPId);
		Optional<Institution> optional = institutionService.findInstitution(institutionID);

		if (!optional.isPresent()) {
			return null;
		}
		return converter.institutionToJAX(optional.get());
	}

	@ApiOperation(value = "Remove an Institution logically by his institution-id as parameter")
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

	@ApiOperation(value = "Find and return a list of Institution by the Traegerschaft as parameter")
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



	@ApiOperation(value = "Find and return a list of all Institutionen")
	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxInstitution> getAllInstitutionen() {
		return institutionService.getAllInstitutionen().stream()
			.map(inst -> converter.institutionToJAX(inst))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Find and return a list of all active Institutionen. An active Institution is a Institution where the active flag is true")
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

	@ApiOperation(value = "Find and return a list of all Institutionen of the currently logged in Benutzer. Retruns all for admins")
	@Nonnull
	@GET
	@Path("/currentuser")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxInstitution> getAllowedInstitutionenForCurrentBenutzer() {
		return institutionService.getAllowedInstitutionenForCurrentBenutzer().stream()
			.map(inst -> converter.institutionToJAX(inst))
			.collect(Collectors.toList());
	}
}
