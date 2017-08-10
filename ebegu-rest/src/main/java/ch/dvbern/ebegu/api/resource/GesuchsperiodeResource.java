package ch.dvbern.ebegu.api.resource;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
import ch.dvbern.ebegu.api.dtos.JaxAbstractDateRangedDTO;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsperiode;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.Validate;

/**
 * REST Resource fuer Gesuchsperiode
 */
@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
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
		// Überprüfen, ob der Statusübergang zulässig ist
		GesuchsperiodeStatus gesuchsperiodeStatusBisher = gesuchsperiode.getStatus();

		Gesuchsperiode convertedGesuchsperiode = converter.gesuchsperiodeToEntity(gesuchsperiodeJAXP, gesuchsperiode);
		Gesuchsperiode persistedGesuchsperiode = this.gesuchsperiodeService.saveGesuchsperiode(convertedGesuchsperiode, gesuchsperiodeStatusBisher);

		return converter.gesuchsperiodeToJAX(persistedGesuchsperiode);
	}

	@Nullable
	@GET
	@Path("/gesuchsperiode/{gesuchsperiodeId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuchsperiode findGesuchsperiode(
		@Nonnull @NotNull @PathParam("gesuchsperiodeId") JaxId gesuchsperiodeJAXPId) {

		Validate.notNull(gesuchsperiodeJAXPId.getId());
		String gesuchsperiodeID = converter.toEntityId(gesuchsperiodeJAXPId);
		Optional<Gesuchsperiode> optional = gesuchsperiodeService.findGesuchsperiode(gesuchsperiodeID);

		return optional.map(gesuchsperiode -> converter.gesuchsperiodeToJAX(gesuchsperiode)).orElse(null);
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
			.sorted(Comparator.comparing(JaxAbstractDateRangedDTO::getGueltigAb).reversed())
			.collect(Collectors.toList());
	}

	@Nonnull
	@GET
	@Path("/active")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxGesuchsperiode> getAllActiveGesuchsperioden() {
		return gesuchsperiodeService.getAllActiveGesuchsperioden().stream()
			.map(gesuchsperiode -> converter.gesuchsperiodeToJAX(gesuchsperiode))
			.collect(Collectors.toList());
	}

	@Nonnull
	@GET
	@Path("/unclosed")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxGesuchsperiode> getAllNichtAbgeschlosseneGesuchsperioden() {
		return gesuchsperiodeService.getAllNichtAbgeschlosseneGesuchsperioden().stream()
			.map(gesuchsperiode -> converter.gesuchsperiodeToJAX(gesuchsperiode))
			.sorted(Comparator.comparing(JaxAbstractDateRangedDTO::getGueltigAb).reversed())
			.collect(Collectors.toList());
	}

	@Nonnull
	@GET
	@Path("/unclosed/{fallId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxGesuchsperiode> getAllNichtAbgeschlosseneNichtVerwendeteGesuchsperioden(@Nonnull @PathParam("fallId") String fallId) {
		return gesuchsperiodeService.getAllNichtAbgeschlosseneNichtVerwendeteGesuchsperioden(fallId).stream()
			.map(gesuchsperiode -> converter.gesuchsperiodeToJAX(gesuchsperiode))
			.sorted(Comparator.comparing(JaxAbstractDateRangedDTO::getGueltigAb).reversed())
			.collect(Collectors.toList());
	}
}
