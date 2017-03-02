package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxMahnung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.MahnungService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.Validate;

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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Resource fuer Mahnungen
 */
@Path("mahnung")
@Stateless
@Api
public class MahnungResource {

	@Inject
	private GesuchService gesuchService;

	@Inject
	private MahnungService mahnungService;

	@Inject
	private JaxBConverter converter;


	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMahnung save(
		@Nonnull @NotNull JaxMahnung mahnungJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(mahnungJAXP);
		Mahnung mahnung = converter.mahnungToEntity(mahnungJAXP, new Mahnung());
		Mahnung persistedMahnung = mahnungService.createMahnung(mahnung);


		return converter.mahnungToJAX(persistedMahnung);
	}

	@Nullable
	@GET
	@Path("/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxMahnung> findMahnungen(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) throws EbeguException {
		Validate.notNull(gesuchJAXPId.getId());
		String gesuchID = converter.toEntityId(gesuchJAXPId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchID);

		if (!gesuchOptional.isPresent()) {
			return null;
		}
		Gesuch gesuchToReturn = gesuchOptional.get();

		return mahnungService.findMahnungenForGesuch(gesuchToReturn).stream()
			.map(mahnung -> converter.mahnungToJAX(mahnung))
			.collect(Collectors.toList());
	}

	@Nonnull
	@PUT
	@Path("/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response mahnlaufBeenden(@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) {
		Validate.notNull(gesuchJAXPId.getId());
		String gesuchID = converter.toEntityId(gesuchJAXPId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchID);

		if (!gesuchOptional.isPresent()) {
			return Response.serverError().build();
		}
		Gesuch gesuchToReturn = gesuchOptional.get();
		mahnungService.mahnlaufBeenden(gesuchToReturn);
		return Response.ok().build();
	}

	@Nonnull
	@GET
	@Path("/bemerkungen/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public String getInitialeBemerkungen(@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) {
		Validate.notNull(gesuchJAXPId.getId());
		String gesuchID = converter.toEntityId(gesuchJAXPId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchID);

		if (!gesuchOptional.isPresent()) {
			return "";
		}
		Gesuch gesuch = gesuchOptional.get();
		return mahnungService.getInitialeBemerkungen(gesuch);
	}
}
