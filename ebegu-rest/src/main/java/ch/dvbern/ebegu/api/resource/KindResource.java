package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxKind;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.KindService;
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
 * REST Resource fuer Kinder
 */
@Path("kinder")
@Stateless
@Api
public class KindResource {

	@Inject
	private KindService kindService;

	@Inject
	private JaxBConverter converter;

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxKind saveKind(
		@Nonnull @NotNull @Valid JaxKind kindJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Kind kind;
		if (kindJAXP.getId() != null) {
			Optional<Kind> optional = kindService.findKind(converter.toEntityId(kindJAXP.getId()));
			kind = optional.isPresent() ? optional.get() : new Kind();
		} else {
			kind = new Kind();
		}
		Kind convertedKind = converter.kindToEntity(kindJAXP, kind);

		Kind persistedKind = this.kindService.saveKind(convertedKind);
		return converter.kindToJAX(persistedKind);
	}

	@Nullable
	@GET
	@Path("/{kindId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxKind findKind(
		@Nonnull @NotNull JaxId kindJAXPId) throws EbeguException {

		Validate.notNull(kindJAXPId.getId());
		String kindID = converter.toEntityId(kindJAXPId);
		Optional<Kind> optional = kindService.findKind(kindID);

		if (!optional.isPresent()) {
			return null;
		}
		Kind kindToReturn = optional.get();

		return converter.kindToJAX(kindToReturn);
	}

	@Nullable
	@DELETE
	@Path("/{kindId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeKind(
		@Nonnull @NotNull @PathParam("kindId") JaxId kindJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(kindJAXPId.getId());
		kindService.removeKind(converter.toEntityId(kindJAXPId));
		return Response.ok().build();
	}

	@Nonnull
	@GET
	@Path("/gesuch/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxKind> getAllKinderFromGesuch(
		@Nullable @QueryParam("gesuchId") JaxId gesuchJAXPId) {

		Validate.notNull(gesuchJAXPId.getId());
		return kindService.getAllKinderFromGesuch(converter.toEntityId(gesuchJAXPId)).stream()
			.map(kinder -> converter.kindToJAX(kinder))
			.collect(Collectors.toList());
	}

}
