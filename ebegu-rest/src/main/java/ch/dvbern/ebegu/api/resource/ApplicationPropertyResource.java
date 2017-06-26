package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxApplicationProperties;
import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.entities.ApplicationProperty;
import ch.dvbern.ebegu.enums.ApplicationPropertyKey;
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
import java.util.Comparator;
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

	@Inject
	private EbeguConfiguration ebeguConfiguration;


	@ApiOperation(value = "Find a property by its unique name (called key)", response = JaxApplicationProperties.class)
	@Nullable
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/key/{key}")
	public JaxApplicationProperties getByKey(
		@Nonnull @PathParam("key") String keyParam,
		@Context HttpServletResponse response) {

		Optional<ApplicationProperty> propertyFromDB = this.applicationPropertyService.readApplicationProperty(keyParam);
		propertyFromDB.orElseThrow(() -> new EbeguEntityNotFoundException("getByKey", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, keyParam));
		return converter.applicationPropertyToJAX(propertyFromDB.get());
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@ApiOperation(value = "Are we in development mode?", response = Boolean.class)
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	@Path("/public/devmode")
	public Response isDevMode(@Context HttpServletResponse response) {
		return Response.ok(ebeguConfiguration.getIsDevmode()).build();
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	@Path("/public/dummy")
	public Response isDummyLoginEnabled(@Context HttpServletResponse response) {
		return Response.ok(ebeguConfiguration.isDummyLoginEnabled()).build();
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@ApiOperation(value = "Returns background Color for the current System", response = String.class)
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/public/background")
	public JaxApplicationProperties getBackgroundColor(@Context HttpServletResponse response) {
		Optional<ApplicationProperty> propertyFromDB = this.applicationPropertyService.readApplicationProperty(ApplicationPropertyKey.BACKGROUND_COLOR);
		ApplicationProperty prop = propertyFromDB.orElse(new ApplicationProperty(ApplicationPropertyKey.BACKGROUND_COLOR, "#FFFFFF"));
		return converter.applicationPropertyToJAX(prop);
	}

	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxApplicationProperties> getAllApplicationProperties() {
		return applicationPropertyService.getAllApplicationProperties().stream()
			.sorted(Comparator.comparing(o -> o.getName().name()))
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

		ApplicationProperty modifiedProperty = this.applicationPropertyService.saveOrUpdateApplicationProperty(Enum.valueOf(ApplicationPropertyKey.class, key), value);

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

		ApplicationProperty modifiedProperty = this.applicationPropertyService.saveOrUpdateApplicationProperty(Enum.valueOf(ApplicationPropertyKey.class, key), value);

		return converter.applicationPropertyToJAX(modifiedProperty);
	}


	@Nullable
	@DELETE
	@Path("/{key}")
	@Consumes(MediaType.WILDCARD)
	public Response remove(@Nonnull @PathParam("key") String keyParam, @Context HttpServletResponse response) {
		applicationPropertyService.removeApplicationProperty(Enum.valueOf(ApplicationPropertyKey.class, keyParam));
		return Response.ok().build();
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@ApiOperation(value = "Are we in Testmode for Zahlungen?", response = Boolean.class)
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	@Path("/public/zahlungentestmode")
	public Response isZahlungenTestMode(@Context HttpServletResponse response) {
		return Response.ok(ebeguConfiguration.getIsZahlungenTestMode()).build();
	}
}
