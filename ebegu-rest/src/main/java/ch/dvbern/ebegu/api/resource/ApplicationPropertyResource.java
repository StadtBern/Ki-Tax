package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxApplicationProperties;
import ch.dvbern.ebegu.entities.ApplicationProperty;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.ApplicationPropertyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Resource fuer ApplicationProperties
 */
@Path("application-properties")
@Stateless
@Api
public class ApplicationPropertyResource {

	@Inject
	private ApplicationPropertyService applicationPropertyService;

	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Find a property by its unique name (called key)", response = JaxApplicationProperties.class)
	@Nullable
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{key}")
	public JaxApplicationProperties getByKey(
		@Nonnull @PathParam("key") String keyParam,
		@Context HttpServletResponse response) {

		Optional<ApplicationProperty> propertyFromDB = this.applicationPropertyService.readApplicationProperty(keyParam);
		propertyFromDB.orElseThrow(() -> new EbeguEntityNotFoundException("getByKey", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, keyParam));
		return converter.applicationPropertyToJAX(propertyFromDB.get());
	}

	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxApplicationProperties> getAllApplicationProperties() {
		return applicationPropertyService.getAllApplicationProperties().stream()
			.map(ap -> converter.applicationPropertyToJAX(ap))
			.collect(Collectors.toList());
	}


	@ApiOperation(value = "Create a new ApplicationProperty with the given key and value",
		response = JaxApplicationProperties.class,
		consumes = MediaType.TEXT_PLAIN)
	@Nullable
	@POST
	@Path("/{key}")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response create(
		@Nonnull @NotNull @PathParam("key") String key,
		@Nonnull @NotNull String value,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		ApplicationProperty modifiedProperty = this.applicationPropertyService.saveOrUpdateApplicationProperty(key, value);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(ApplicationPropertyResource.class)
			.path("/" + modifiedProperty.getName())
			.build();

		return Response.created(uri).entity(converter.applicationPropertyToJAX(modifiedProperty)).build();


	}

	@ApiOperation(value = "Aktualisiert ein bestehendes ApplicationProperty",
			response = JaxApplicationProperties.class,
			consumes = MediaType.TEXT_PLAIN)
	@Nullable
	@PUT
	@Path("/{key}")
	@Consumes(MediaType.TEXT_PLAIN)
	public JaxApplicationProperties update(
		@Nonnull @PathParam("key") String key,
		@Nonnull @NotNull String value,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		ApplicationProperty modifiedProperty = this.applicationPropertyService.saveOrUpdateApplicationProperty(key, value);

		return converter.applicationPropertyToJAX(modifiedProperty);
	}


	@Nullable
	@DELETE
	@Path("/{key}")
	@Consumes(MediaType.WILDCARD)
	public Response remove(
		@Nonnull @PathParam("key") String keyParam,
		@Context HttpServletResponse response) {

		applicationPropertyService.removeApplicationProperty(keyParam);
		return Response.ok().build();
	}


}
