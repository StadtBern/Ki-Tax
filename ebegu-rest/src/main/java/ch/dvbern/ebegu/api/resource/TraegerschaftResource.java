package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxTraegerschaft;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.TraegerschaftService;
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
 * REST Resource fuer Traegerschaft
 */
@Path("traegerschaften")
@Stateless
@Api
public class TraegerschaftResource {

	@Inject
	private TraegerschaftService traegerschaftService;

	@Inject
	private JaxBConverter converter;

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxTraegerschaft saveTraegerschaft(
		@Nonnull @NotNull @Valid JaxTraegerschaft traegerschaftJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Traegerschaft traegerschaft;
		if (traegerschaftJAXP.getId() != null) {
			Optional<Traegerschaft> optional = traegerschaftService.findTraegerschaft(converter.toEntityId(traegerschaftJAXP.getId()));
			traegerschaft = optional.isPresent() ? optional.get() : new Traegerschaft();
		} else {
			traegerschaft = new Traegerschaft();
		}
		Traegerschaft convertedTraegerschaft = converter.traegerschaftToEntity(traegerschaftJAXP, traegerschaft);

		Traegerschaft persistedTraegerschaft = this.traegerschaftService.saveTraegerschaft(convertedTraegerschaft);

		return converter.traegerschaftToJAX(persistedTraegerschaft);
	}

	@Nullable
	@GET
	@Path("/{traegerschaftId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxTraegerschaft findTraegerschaft(
		@Nonnull @NotNull @PathParam("traegerschaftId") JaxId traegerschaftJAXPId) throws EbeguException {

		Validate.notNull(traegerschaftJAXPId.getId());
		String traegerschaftID = converter.toEntityId(traegerschaftJAXPId);
		Optional<Traegerschaft> optional = traegerschaftService.findTraegerschaft(traegerschaftID);

		if (!optional.isPresent()) {
			return null;
		}
		return converter.traegerschaftToJAX(optional.get());
	}

	@Nullable
	@DELETE
	@Path("/{traegerschaftId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeTraegerschaft(
		@Nonnull @NotNull @PathParam("traegerschaftId") JaxId traegerschaftJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(traegerschaftJAXPId.getId());
		traegerschaftService.removeTraegerschaft(converter.toEntityId(traegerschaftJAXPId));
		return Response.ok().build();
	}

	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxTraegerschaft> getAllTraegerschaften() {
		return traegerschaftService.getAllTraegerschaften().stream()
			.map(traegerschaft -> converter.traegerschaftToJAX(traegerschaft))
			.collect(Collectors.toList());
	}

}
