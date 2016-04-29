package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxFachstelle;
import ch.dvbern.ebegu.entities.Fachstelle;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.FachstelleService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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
 * REST Resource fuer Fachstellen
 */
@Path("fachstellen")
@Stateless
@Api
public class FachstelleResource {

	@Inject
	private FachstelleService fachstelleService;

	@Inject
	private JaxBConverter converter;

	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
		@Nonnull @NotNull JaxFachstelle fachstelleJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Fachstelle convertedFachstelle = converter.fachstelleToEntity(fachstelleJAXP, new Fachstelle());
		Fachstelle persistedFachstelle = this.fachstelleService.saveFachstelle(convertedFachstelle);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(FachstelleResource.class)
			.path("/" + persistedFachstelle.getId())
			.build();

		return Response.created(uri).entity(converter.fachstelleToJAX(persistedFachstelle)).build();

	}

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFachstelle update(
		@Nonnull @NotNull JaxFachstelle fachstelleJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(fachstelleJAXP.getId());
		Optional<Fachstelle> fachstelleFromDB = fachstelleService.findFachstelle(fachstelleJAXP.getId());
		fachstelleFromDB.orElseThrow(() -> new EbeguEntityNotFoundException("update", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, fachstelleJAXP.getId()));
		Fachstelle fachstelleToMerge = converter.fachstelleToEntity(fachstelleJAXP, fachstelleFromDB.get());
		Fachstelle modifiedFachstelle = this.fachstelleService.saveFachstelle(fachstelleToMerge);

		return converter.fachstelleToJAX(modifiedFachstelle);
	}

	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxFachstelle> getAllFachstellen() {
		return fachstelleService.getAllFachstellen().stream()
			.map(ap -> converter.fachstelleToJAX(ap))
			.collect(Collectors.toList());
	}

	@Nullable
	@DELETE
	@Path("/{fachstelleJAXP}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response remove(
		@Nonnull @PathParam("fachstelleJAXP") String fachstelleJAXP,
		@Context HttpServletRequest request,
		@Context HttpServletResponse response) throws EbeguException {

		fachstelleService.removeFachstelle(fachstelleJAXP);
		return Response.ok().build();
	}

	@Nullable
	@GET
	@Path("/{fachstelleId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFachstelle findFachstelle(
		@Nonnull @NotNull @PathParam("fachstelleId") String fachstelleId) throws EbeguException {

		Optional<Fachstelle> fachstelleFromDB = fachstelleService.findFachstelle(fachstelleId);

		if (!fachstelleFromDB.isPresent()) {
			return null;
		}

		return converter.fachstelleToJAX(fachstelleFromDB.get());

	}

}
