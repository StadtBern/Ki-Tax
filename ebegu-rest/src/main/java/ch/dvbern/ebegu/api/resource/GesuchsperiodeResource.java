package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsperiode;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
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
 * REST Resource fuer Gesuchsperiode
 */
@Path("gesuchsperioden")
@Stateless
@Api(description = "Resource welche zum bearbeiten der Gesuchsperiode dient")
public class GesuchsperiodeResource {

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private JaxBConverter converter;

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuchsperiode saveGesuchsperiode(
		@Nonnull @NotNull @Valid JaxGesuchsperiode gesuchsperiodeJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Gesuchsperiode gesuchsperiode = new Gesuchsperiode();
		if (gesuchsperiodeJAXP.getId() != null) {
			Optional<Gesuchsperiode> optional = gesuchsperiodeService.findGesuchsperiode(gesuchsperiodeJAXP.getId());
			gesuchsperiode = optional.isPresent() ? optional.get() : new Gesuchsperiode();
		}

		Gesuchsperiode convertedGesuchsperiode = converter.gesuchsperiodeToEntity(gesuchsperiodeJAXP, gesuchsperiode);
		Gesuchsperiode persistedGesuchsperiode = this.gesuchsperiodeService.saveGesuchsperiode(convertedGesuchsperiode);

		return converter.gesuchsperiodeToJAX(persistedGesuchsperiode);
	}

	@Nullable
	@GET
	@Path("/{gesuchsperiodeId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuchsperiode findGesuchsperiode(
		@Nonnull @NotNull @PathParam("gesuchsperiodeId") JaxId gesuchsperiodeJAXPId) {

		Validate.notNull(gesuchsperiodeJAXPId.getId());
		String gesuchsperiodeID = converter.toEntityId(gesuchsperiodeJAXPId);
		Optional<Gesuchsperiode> optional = gesuchsperiodeService.findGesuchsperiode(gesuchsperiodeID);

		if (!optional.isPresent()) {
			return null;
		}
		return converter.gesuchsperiodeToJAX(optional.get());
	}

	@Nullable
	@DELETE
	@Path("/{gesuchsperiodeId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeGesuchsperiode(
		@Nonnull @NotNull @PathParam("gesuchsperiodeId") JaxId gesuchsperiodeJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(gesuchsperiodeJAXPId.getId());
		gesuchsperiodeService.removeGesuchsperiode(converter.toEntityId(gesuchsperiodeJAXPId));
		return Response.ok().build();
	}

	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxGesuchsperiode> getAllGesuchsperioden() {

		return gesuchsperiodeService.getAllGesuchsperioden().stream()
			.map(gesuchsperiode -> converter.gesuchsperiodeToJAX(gesuchsperiode))
			.collect(Collectors.toList());
	}

}
