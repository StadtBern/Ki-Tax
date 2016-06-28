package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxFall;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.FallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;

/**
 * Resource fuer Fall
 */
@Path("falle")
@Stateless
@Api
public class FallResource {

	@Inject
	private FallService fallService;
	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Creates a new Fall in the database. The transfer object also has a relation to Gesuch " +
			"which is stored in the database as well.")
	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
		@Nonnull @NotNull JaxFall fallJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Fall convertedFall = converter.fallToEntity(fallJAXP, new Fall());
		Fall persistedFall = this.fallService.createFall(convertedFall);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(FallResource.class)
			.path("/" + persistedFall.getId())
			.build();

		JaxFall jaxFall = converter.fallToJAX(persistedFall);

		return Response.created(uri).entity(jaxFall).build();
	}

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFall update(
		@Nonnull @NotNull JaxFall fallJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		return null;
	}

	@Nullable
	@GET
	@Path("/{fallId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFall findFall(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallJAXPId) throws EbeguException {
		Validate.notNull(fallJAXPId.getId());
		String fallID = converter.toEntityId(fallJAXPId);
		Optional<Fall> fallOptional = fallService.findFall(fallID);

		if (!fallOptional.isPresent()) {
			return null;
		}
		Fall fallToReturn = fallOptional.get();
		return converter.fallToJAX(fallToReturn);
	}

}
