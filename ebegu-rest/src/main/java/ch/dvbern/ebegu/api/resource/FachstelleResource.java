package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxFachstelle;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Fachstelle;
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
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFachstelle saveFachstelle(
		@Nonnull @NotNull @Valid JaxFachstelle fachstelleJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Fachstelle fachstelle;
		if (fachstelleJAXP.getId() != null) {
			Optional<Fachstelle> optional = fachstelleService.findFachstelle(converter.toEntityId(fachstelleJAXP.getId()));
			fachstelle = optional.isPresent() ? optional.get() : new Fachstelle();
		} else {
			fachstelle = new Fachstelle();
		}
		Fachstelle convertedFachstelle = converter.fachstelleToEntity(fachstelleJAXP, fachstelle);

		Fachstelle persistedFachstelle = this.fachstelleService.saveFachstelle(convertedFachstelle);

		return converter.fachstelleToJAX(persistedFachstelle);
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
