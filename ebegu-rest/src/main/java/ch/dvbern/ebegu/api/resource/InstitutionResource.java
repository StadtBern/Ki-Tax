package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxInstitution;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.InstitutionService;
import io.swagger.annotations.Api;
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

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxInstitution saveInstitution(
		@Nonnull @NotNull @Valid JaxInstitution institutionJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Institution institution;
		if (institutionJAXP.getId() != null) {
			Optional<Institution> optional = institutionService.findInstitution(institutionJAXP.getId());
			institution = optional.isPresent() ? optional.get() : new Institution();
		} else {
			institution = new Institution();
		}

		Institution convertedInstitution = converter.institutionToEntity(institutionJAXP, institution);
		Institution persistedInstitution = institutionService.saveInstitution(convertedInstitution);

		return converter.institutionToJAX(persistedInstitution);

	}

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

	@Nullable
	@DELETE
	@Path("/{institutionId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeInstitution(
		@Nonnull @NotNull @PathParam("institutionId") JaxId institutionJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(institutionJAXPId.getId());
		institutionService.removeInstitution(converter.toEntityId(institutionJAXPId));
		return Response.ok().build();
	}

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

	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxInstitution> getAllFachstellen() {
		return institutionService.getAllInstitutionen().stream()
			.map(institution -> converter.institutionToJAX(institution))
			.collect(Collectors.toList());
	}

}
