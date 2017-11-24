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
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Resource fuer ApplicationProperties
 */
@Path("application-properties")
@Stateless
@Api(description = "Resource zum Lesen der Applikationsproperties")
@PermitAll
public class ApplicationPropertyResource {

	@Inject
	private ApplicationPropertyService applicationPropertyService;

	@Inject
	private JaxBConverter converter;

	@Inject
	private EbeguConfiguration ebeguConfiguration;

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationPropertyResource.class.getSimpleName());

	@ApiOperation(value = "Find a property by its unique name (called key)", response = JaxApplicationProperties.class)
	@Nullable
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/key/{key}")
	public JaxApplicationProperties getByKey(
		@Nonnull @PathParam("key") String keyParam,
		@Context HttpServletResponse response) {

		ApplicationProperty propertyFromDB = this.applicationPropertyService.readApplicationProperty(keyParam)
			.orElseThrow(() -> new EbeguEntityNotFoundException("getByKey", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, keyParam));
		return converter.applicationPropertyToJAX(propertyFromDB);
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

	@ApiOperation(value = "converts the list of whitelisted mimetypes (for uploads) into a list of file-extensions and "
		+ "retunrs it as a property ", response = JaxApplicationProperties.class)
	@Nullable
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/public/UPLOAD_FILETYPES_WHITELIST")
	public JaxApplicationProperties getWhitelist(
		@Context HttpServletResponse response) {

		final Collection<String> whitelist = this.applicationPropertyService.readMimeTypeWhitelist();
		MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();

		final List<String> extensions = whitelist.stream().map(mimetype -> {
			try {
				return allTypes.forName(mimetype).getExtension();
			} catch (MimeTypeException e) {
				LOG.error("Could not find extension for mime type {}", mimetype);
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());

		final String list = StringUtils.join(extensions, ",");
		ApplicationProperty applicationProperty = new ApplicationProperty(ApplicationPropertyKey.UPLOAD_FILETYPES_WHITELIST, list);
		return converter.applicationPropertyToJAX(applicationProperty);
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@ApiOperation(value = "Is Dummy-Login enabled?", response = Boolean.class)
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

	@ApiOperation(value = "Returns all application properties", responseContainer = "List", response = JaxApplicationProperties.class)
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
		response = JaxApplicationProperties.class, consumes = MediaType.TEXT_PLAIN)
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
		response = JaxApplicationProperties.class, consumes = MediaType.TEXT_PLAIN)
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

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@ApiOperation(value = "Removes an application property")
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

	@RolesAllowed({ SUPER_ADMIN })
	@ApiOperation(value = "Gibt den Wert des Properties zurück", response = Boolean.class)
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	@Path("/property/{key}")
	public Response getProperty(@Nonnull @PathParam("key") String keyParam, @Context HttpServletResponse response) {
		if (keyParam.startsWith("ebegu")) {
			return Response.ok(System.getProperty(keyParam)).build();
		}
		return Response.noContent().build();
	}
}
