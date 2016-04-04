package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.dtos.JaxFamilienSituation;
import ch.dvbern.ebegu.api.dtos.JaxId;
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

/**
 * Resource fuer Familiensituation
 */
@Path("familiensituation")
@Stateless
@Api
public class FamilienSituationResource {

	@Inject
	private FamiliensituationService familiensituationService;

	@ApiOperation(value = "Creates a new Familiensituation in the database. ")
	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
		@Nonnull @NotNull JaxFamilienSituation familiensituationJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		return null;
	}

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFamilienSituation update(
		@Nonnull @NotNull JaxFamilienSituation familiensituationJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		return null;
	}

	@Nullable
	@GET
	@Path("/{familiensituationId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFamilienSituation findFamiliensituation(
		@Nonnull @NotNull JaxId fallJAXPId) throws EbeguException {

		return null;
	}

}
