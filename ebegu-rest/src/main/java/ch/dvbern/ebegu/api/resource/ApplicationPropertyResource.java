package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.resource.util.JaxBConverter;
import ch.dvbern.ebegu.entities.ApplicationProperty;
import ch.dvbern.ebegu.services.ApplicationPropertyService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * Resource fuer ApplicationProperties
 */
@Path("application-properties")
@Stateless
public class ApplicationPropertyResource {

	@Inject
	private ApplicationPropertyService applicationPropertyService;

	@Inject
	private JaxBConverter converter;


	@Nullable
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{key}")
	public Response getByKey(
		@Nonnull @PathParam("key") String keyParam,
		@Context HttpServletResponse response) {

		ApplicationProperty propertyFromDB = this.applicationPropertyService.readApplicationProperty(keyParam);

		//todo homa handle does not exist error
		return Response.ok(converter.applicationPropertieToJAX(propertyFromDB)).build();
//		return Response.ok(converter.benutzerToResource(benutzer.get())).build();

	}

	@Nullable
	@POST
	@Path("/{key}")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response create(
		@Nonnull @NotNull @PathParam("key") String key,
		@Nonnull @NotNull String value,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EntityNotFoundException {

		ApplicationProperty modifiedProperty = this.applicationPropertyService.saveOrUpdateApplicationProperty(key, value);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(ApplicationPropertyResource.class)
			.path("/" + modifiedProperty.getName())
			.build();

		return Response.created(uri).build();
	}

	@Nullable
	@PUT
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(
		@Nonnull @PathParam("key") String key,
		@Nullable String value,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		ApplicationProperty modifiedProperty = this.applicationPropertyService.saveOrUpdateApplicationProperty(key, value);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(ApplicationPropertyResource.class)
			.path("/" + modifiedProperty.getName())
			.build();

		return Response.created(uri).build();
	}


}
