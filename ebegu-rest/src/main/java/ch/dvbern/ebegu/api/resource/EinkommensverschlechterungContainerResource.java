package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxEinkommensverschlechterungContainer;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.EinkommensverschlechterungContainerService;
import ch.dvbern.ebegu.services.GesuchstellerService;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;

/**
 * REST Resource fuer EinkommensverschlechterungContainer
 */
@Path("einkommensverschlechterungContainer")
@Stateless
@Api
public class EinkommensverschlechterungContainerResource {

	@Inject
	private EinkommensverschlechterungContainerService einkommensverschlechterungContainerService;

	@Inject
	private GesuchstellerService gesuchstellerService;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private JaxBConverter converter;

	@ApiOperation(value = "Create a new EinkommensverschlechterungContainer in the database. The transfer object also has a relation to EinkommensverschlechterungContainer, " +
		"it is stored in the database as well.")
	@Nullable
	@PUT
	@Path("/{gesuchstellerId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveEinkommensverschlechterungContainer(
		@Nonnull @NotNull @PathParam("gesuchstellerId") JaxId gesuchstellerId,
		@Nonnull @NotNull @Valid JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<Gesuchsteller> gesuchsteller = gesuchstellerService.findGesuchsteller(gesuchstellerId.getId());
		if (gesuchsteller.isPresent()) {
			EinkommensverschlechterungContainer convertedFinSitCont = converter.einkommensverschlechterungContainerToStorableEntity(einkommensverschlechterungContainerJAXP);
			convertedFinSitCont.setGesuchsteller(gesuchsteller.get());
			EinkommensverschlechterungContainer persistedEinkommensverschlechterungContainer =
				einkommensverschlechterungContainerService.saveEinkommensverschlechterungContainer(convertedFinSitCont);

			URI uri = uriInfo.getBaseUriBuilder()
				.path(EinkommensverschlechterungContainerResource.class)
				.path("/" + persistedEinkommensverschlechterungContainer.getId())
				.build();

			JaxEinkommensverschlechterungContainer jaxEinkommensverschlechterungContainer = converter.einkommensverschlechterungContainerToJAX(persistedEinkommensverschlechterungContainer);
			return Response.created(uri).entity(jaxEinkommensverschlechterungContainer).build();
		}
		throw new EbeguEntityNotFoundException("saveEinkommensverschlechterungContainer", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchstellerId invalid: " + gesuchstellerId.getId());
	}


	@Nullable
	@GET
	@Path("/{einkommensverschlechterungContainerId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxEinkommensverschlechterungContainer findEinkommensverschlechterungContainer(
		@Nonnull @NotNull @PathParam("einkommensverschlechterungContainerId") JaxId einkommensverschlechterungContainerId) throws EbeguException {

		Validate.notNull(einkommensverschlechterungContainerId.getId());
		String einkommensverschlechterungContainerID = converter.toEntityId(einkommensverschlechterungContainerId);
		Optional<EinkommensverschlechterungContainer> optional = einkommensverschlechterungContainerService.findEinkommensverschlechterungContainer(einkommensverschlechterungContainerID);

		if (!optional.isPresent()) {
			return null;
		}
		EinkommensverschlechterungContainer einkommensverschlechterungContainerToReturn = optional.get();
		return converter.einkommensverschlechterungContainerToJAX(einkommensverschlechterungContainerToReturn);
	}
}
