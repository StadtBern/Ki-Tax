package ch.dvbern.ebegu.api.resource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxFachstelle;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Fachstelle;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.FachstelleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

/**
 * REST Resource fuer Fachstellen
 */
@Path("fachstellen")
@Stateless
@Api(description = "Resource zur Verwaltung von Fachstellen")
public class FachstelleResource {

	@Inject
	private FachstelleService fachstelleService;

	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Saves a Fachstelle in the database", response = JaxFachstelle.class)
	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFachstelle saveFachstelle(
		@Nonnull @NotNull @Valid JaxFachstelle fachstelleJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Fachstelle  fachstelle = new Fachstelle();
		if (fachstelleJAXP.getId() != null) {
			Optional<Fachstelle> optional = fachstelleService.findFachstelle(fachstelleJAXP.getId());
			fachstelle = optional.orElse(new Fachstelle());
		}
		Fachstelle convertedFachstelle = converter.fachstelleToEntity(fachstelleJAXP, fachstelle);

		Fachstelle persistedFachstelle = this.fachstelleService.saveFachstelle(convertedFachstelle);
		return converter.fachstelleToJAX(persistedFachstelle);
	}

	@ApiOperation(value = "Returns all Fachstellen", responseContainer = "List", response = JaxFachstelle.class)
	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxFachstelle> getAllFachstellen() {
		return fachstelleService.getAllFachstellen().stream()
			.map(ap -> converter.fachstelleToJAX(ap))
			.collect(Collectors.toList());
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@ApiOperation(value = "Removes a Fachstelle from the database", response = Void.class)
	@Nullable
	@DELETE
	@Path("/{fachstelleJAXPID}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response remove(
		@Nonnull @NotNull @PathParam("fachstelleJAXPID") JaxId fachstelleJAXPID,
		@Context HttpServletRequest request,
		@Context HttpServletResponse response) throws EbeguException {

			Validate.notNull(fachstelleJAXPID.getId());
			fachstelleService.removeFachstelle(converter.toEntityId(fachstelleJAXPID));
			return Response.ok().build();
	}

	@ApiOperation(value = "Returns the Fachstelle with the given Id", response = JaxFachstelle.class)
	@Nullable
	@GET
	@Path("/{fachstelleId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFachstelle findFachstelle(
		@Nonnull @NotNull @PathParam("fachstelleId") JaxId fachstelleId) throws EbeguException {

		Validate.notNull(fachstelleId.getId());
		Optional<Fachstelle> fachstelleFromDB = fachstelleService.findFachstelle(converter.toEntityId(fachstelleId));

		if (!fachstelleFromDB.isPresent()) {
			return null;
		}
		return converter.fachstelleToJAX(fachstelleFromDB.get());
	}
}
