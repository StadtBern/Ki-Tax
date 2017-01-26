package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxFall;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.FallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import java.util.Optional;

/**
 * Resource fuer Fall
 */
@Path("falle")
@Stateless
@Api
public class FallResource {

	@Inject
	private FallService fallService;
	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Creates a new Fall in the database. The transfer object also has a relation to Gesuch " +
		"which is stored in the database as well.")
	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFall saveFall(
		@Nonnull @NotNull @Valid JaxFall fallJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Fall  fall = new Fall();
		if (fallJAXP.getId() != null) {
			Optional<Fall> optional = fallService.findFall(fallJAXP.getId());
			fall = optional.orElse(new Fall());
		}
		Fall convertedFall = converter.fallToEntity(fallJAXP, fall);

		Fall persistedFall = this.fallService.saveFall(convertedFall);
		return converter.fallToJAX(persistedFall);
	}

	@Nullable
	@GET
	@Path("/id/{fallId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFall findFall(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallJAXPId) throws EbeguException {
		Validate.notNull(fallJAXPId.getId());
		String fallID = converter.toEntityId(fallJAXPId);
		Optional<Fall> fallOptional = fallService.findFall(fallID);

		if (!fallOptional.isPresent()) {
			return null;
		}
		Fall fallToReturn = fallOptional.get();
		return converter.fallToJAX(fallToReturn);
	}

	@Nullable
	@GET
	@Path("/currentbenutzer")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFall findFallByCurrentBenutzerAsBesitzer() throws EbeguException {
		Optional<Fall> fallOptional = fallService.findFallByCurrentBenutzerAsBesitzer();
		if (!fallOptional.isPresent()) {
			return null;
		}
		Fall fallToReturn = fallOptional.get();
		return converter.fallToJAX(fallToReturn);
	}

	@ApiOperation(value = "Creates a new Fall in the database with the current user as owner.")
	@NotNull
	@PUT
	@Path("/createforcurrentbenutzer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFall createFallForCurrentGesuchstellerAsBesitzer(
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<Fall> fallOptional = fallService.createFallForCurrentGesuchstellerAsBesitzer();
		if (!fallOptional.isPresent()) {
			return null;
		}
		Fall fallToReturn = fallOptional.get();
		return converter.fallToJAX(fallToReturn);
	}
}
