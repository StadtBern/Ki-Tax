package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxMitteilung;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.MitteilungService;
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
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * resource fuer Mitteilung
 */
@Path("mitteilungen")
@Stateless
@Api(description = "Resource zum verwalten von Mitteilungen")
public class MitteilungResource {

	@Inject
	private MitteilungService mitteilungService;

	@Inject
	private FallService fallService;

	@Inject
	private JaxBConverter converter;


	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung createMitteilung(
		@Nonnull @NotNull @Valid JaxMitteilung mitteilungJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Mitteilung convertedMitteilung = converter.mitteilungToEntity(mitteilungJAXP, new Mitteilung());
		Mitteilung persistedMitteilung = this.mitteilungService.createMitteilung(convertedMitteilung);

		return converter.mitteilungToJAX(persistedMitteilung);
	}

	@Nullable
	@PUT
	@Path("/setgelesen/{mitteilungId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung setMitteilungGelesen(
		@Nonnull @NotNull @PathParam("mitteilungId") JaxId mitteilungId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		final Mitteilung mitteilung = mitteilungService.setMitteilungGelesen(mitteilungId.getId());

		return converter.mitteilungToJAX(mitteilung);
	}

	@Nullable
	@PUT
	@Path("/seterledigt/{mitteilungId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung setMitteilungErledigt(
		@Nonnull @NotNull @PathParam("mitteilungId") JaxId mitteilungId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		final Mitteilung mitteilung = mitteilungService.setMitteilungErledigt(mitteilungId.getId());

		return converter.mitteilungToJAX(mitteilung);
	}

	@Nullable
	@GET
	@Path("/seterledigt/{mitteilungId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung findMitteilung(
		@Nonnull @NotNull @PathParam("mitteilungId") JaxId mitteilungId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(mitteilungId.getId());
		String mitteilungID = converter.toEntityId(mitteilungId);
		Optional<Mitteilung> optional = mitteilungService.findMitteilung(mitteilungID);

		return optional.map(mitteilung -> converter.mitteilungToJAX(mitteilung)).orElse(null);
	}

	@Nullable
	@GET
	@Path("/forrole/{fallId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<JaxMitteilung> getMitteilungenForCurrentRolle(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(fallId.getId());
		String mitteilungID = converter.toEntityId(fallId);
		Optional<Fall> fall = fallService.findFall(mitteilungID);
		if (fall.isPresent()) {
			final Collection<Mitteilung> mitteilungen = mitteilungService.getMitteilungenForCurrentRolle(fall.get());
			return mitteilungen.stream().map(mitteilung -> converter.mitteilungToJAX(mitteilung)).collect(Collectors.toList());
		}
		throw new EbeguEntityNotFoundException("getMitteilungenForCurrentRolle", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "FallID invalid: " + fallId.getId());
	}

	@Nullable
	@GET
	@Path("/posteingang")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<JaxMitteilung> getMitteilungenForPosteingang(
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		final Collection<Mitteilung> mitteilungen = mitteilungService.getMitteilungenForPosteingang();
		return mitteilungen.stream().map(mitteilung -> converter.mitteilungToJAX(mitteilung)).collect(Collectors.toList());
	}
}
