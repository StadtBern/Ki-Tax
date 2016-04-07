package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.FamiliensituationService;
import ch.dvbern.ebegu.services.GesuchService;
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

/**
 * Resource fuer Gesuch
 */
@Path("gesuche")
@Stateless
@Api
public class GesuchResource {

	@Inject
	private GesuchService gesuchService;
	@Inject
	private FamiliensituationService familiensituationService;
	@Inject
	private JaxBConverter converter;

	@ApiOperation(value = "Creates a new Gesuch in the database. The transfer object also has a relation to Familiensituation " +
		"which is stored in the database as well.")
	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
		@Nonnull @NotNull JaxGesuch gesuchJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Gesuch convertedGesuch = converter.gesuchToEntity(gesuchJAXP, new Gesuch());
		Gesuch persistedGesuch = this.gesuchService.createGesuch(convertedGesuch);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(GesuchResource.class)
			.path("/" + persistedGesuch.getId())
			.build();

		JaxGesuch jaxGesuch = converter.gesuchToJAX(persistedGesuch);

		return Response.created(uri).entity(jaxGesuch).build();
	}

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuch update(
		@Nonnull @NotNull JaxGesuch gesuchJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		return null;
	}

	@Nullable
	@GET
	@Path("/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuch findGesuch(
		@Nonnull @NotNull JaxId gesuchJAXPId) throws EbeguException {

		return null;
	}

}
