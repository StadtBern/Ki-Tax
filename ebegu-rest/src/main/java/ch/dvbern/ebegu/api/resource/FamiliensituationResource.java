package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxFamiliensituation;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.FamiliensituationService;
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
import java.util.Objects;
import java.util.Optional;

/**
 * Resource fuer Familiensituation
 */
@Path("familiensituation")
@Stateless
@Api
public class FamiliensituationResource {

	@Inject
	private FamiliensituationService familiensituationService;

	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Creates a new Familiensituation in the database. ")
	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
		@Nonnull @NotNull JaxFamiliensituation familiensituationJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Familiensituation convertedFamiliensituation = converter.familiensituationToEntity(familiensituationJAXP, new Familiensituation());
		Familiensituation persistedFamiliensituation = this.familiensituationService.createFamiliensituation(convertedFamiliensituation);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(FamiliensituationResource.class)
			.path("/" + persistedFamiliensituation.getId())
			.build();

		JaxFamiliensituation jaxGesuchsteller = converter.familiensituationToJAX(persistedFamiliensituation);

		return Response.created(uri).entity(jaxGesuchsteller).build();
	}

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFamiliensituation update(
		@Nonnull @NotNull JaxFamiliensituation familiensituationJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Objects.requireNonNull(familiensituationJAXP.getId());

		Optional<Familiensituation> loadedFamiliensituation = this.familiensituationService.findFamiliensituation(familiensituationJAXP.getId());
		if (!loadedFamiliensituation.isPresent()) {
			throw new EbeguEntityNotFoundException("updateFamiliensituation", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, familiensituationJAXP.getId());
		}
		Familiensituation convertedFamiliensituation = converter.familiensituationToEntity(familiensituationJAXP, loadedFamiliensituation.get());
		Familiensituation persistedFamiliensituation = this.familiensituationService.updateFamiliensituation(convertedFamiliensituation);

		return converter.familiensituationToJAX(persistedFamiliensituation);
	}

	@Nullable
	@GET
	@Path("/{familiensituationId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFamiliensituation findFamiliensituation(
		@Nonnull @NotNull JaxId fallJAXPId) throws EbeguException {

		return null;
	}

}
