package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxEinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.EinkommensverschlechterungInfoService;
import ch.dvbern.ebegu.services.GesuchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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
 * REST Resource fuer Einkommensverschlechterung
 */
@Path("einkommensverschlechterungInfo")
@Stateless
@Api
public class EinkommensverschlechterungInfoResource {

	@Inject
	private EinkommensverschlechterungInfoService einkommensverschlechterungInfoService;
	@Inject
	private GesuchService gesuchService;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Create a new EinkommensverschlechterungInfoContainer in the database.")
	@Nullable
	@PUT
	@Path("/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveEinkommensverschlechterungInfo(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchId,
		@Nonnull @NotNull @Valid JaxEinkommensverschlechterungInfoContainer jaxEinkommensverschlechterungInfoContainer,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchId.getId());
		if (gesuch.isPresent()) {

			EinkommensverschlechterungInfoContainer oldEVData = null;
			EinkommensverschlechterungInfoContainer ekviToMerge = new EinkommensverschlechterungInfoContainer();

			if (jaxEinkommensverschlechterungInfoContainer.getId() != null) {
				Optional<EinkommensverschlechterungInfoContainer> optional = einkommensverschlechterungInfoService.
					findEinkommensverschlechterungInfo(jaxEinkommensverschlechterungInfoContainer.getId());
				ekviToMerge = optional.orElse(new EinkommensverschlechterungInfoContainer());
				oldEVData = new EinkommensverschlechterungInfoContainer();
				ekviToMerge.copyForMutation(oldEVData); //wir muessen uns merken wie die Daten vorher waren damit wir nachher vergleichen koennen
			}
			EinkommensverschlechterungInfoContainer convertedEkvi = converter
				.einkommensverschlechterungInfoContainerToEntity(jaxEinkommensverschlechterungInfoContainer, ekviToMerge);

			EinkommensverschlechterungInfoContainer persistedEkvi = einkommensverschlechterungInfoService
				.updateEinkommensVerschlechterungInfoAndGesuch(gesuch.get(), oldEVData, convertedEkvi);

			URI uri = uriInfo.getBaseUriBuilder()
				.path(EinkommensverschlechterungInfoResource.class)
				.path("/" + convertedEkvi.getId())
				.build();

			JaxEinkommensverschlechterungInfoContainer jaxEinkommensverschlechterungInfoContainerReturn = converter.einkommensverschlechterungInfoContainerToJAX(persistedEkvi);
			return Response.created(uri).entity(jaxEinkommensverschlechterungInfoContainerReturn).build();
		}
		throw new EbeguEntityNotFoundException("saveEinkommensverschlechterungInfo", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchId.getId());
	}
}
