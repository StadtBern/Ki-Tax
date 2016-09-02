package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxErwerbspensumContainer;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.ErwerbspensumService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchstellerService;
import ch.dvbern.ebegu.services.WizardStepService;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Resource fuer Erwerbspensum
 */
@Path("erwerbspensen")
@Stateless
@Api(description = "Resource welche zum bearbeiten des Erwerbspensums dient")
public class ErwerbspensumResource {

	@Inject
	private ErwerbspensumService erwerbspensumService;

	@Inject
	private GesuchstellerService gesuchstellerService;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private GesuchService gesuchService;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Create a new ErwerbspensumContainer in the database. The object also has a relations to Erwerbspensum data Objects, " +
		", those will be created as well")
	@Nonnull
	@PUT
	@Path("/{gesuchstellerId}/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveErwerbspensum(
		@Nonnull @NotNull @PathParam ("gesuchId") JaxId gesuchJAXPId,
		@Nonnull @NotNull @PathParam("gesuchstellerId") JaxId gesuchstellerId,
		@Nonnull @NotNull @Valid JaxErwerbspensumContainer jaxErwerbspensumContainer,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguRuntimeException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchJAXPId.getId());
		if (gesuch.isPresent()) {
			Optional<Gesuchsteller> gesuchsteller = gesuchstellerService.findGesuchsteller(gesuchstellerId.getId());
			if (gesuchsteller.isPresent()) {
				ErwerbspensumContainer convertedEwpContainer = converter.erwerbspensumContainerToStoreableEntity(jaxErwerbspensumContainer);
				convertedEwpContainer.setGesuchsteller(gesuchsteller.get());
				ErwerbspensumContainer storedEwpCont = this.erwerbspensumService.saveErwerbspensum(convertedEwpContainer);

				URI uri = null;
				if (uriInfo != null) {
					uri = uriInfo.getBaseUriBuilder()
						.path(ErwerbspensumResource.class)
						.path("/" + storedEwpCont.getId())
						.build();
				}
				JaxErwerbspensumContainer jaxEwpCont = converter.erwerbspensumContainerToJAX(storedEwpCont);

				wizardStepService.updateSteps(gesuchJAXPId.getId(), convertedEwpContainer,
					storedEwpCont, WizardStepName.ERWERBSPENSUM);

				return Response.created(uri).entity(jaxEwpCont).build();
			}
		}
		throw new EbeguEntityNotFoundException("saveErwerbspensum", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchstellerId invalid: " + gesuchstellerId.getId());
	}

	@ApiOperation(value = "Returns the ErwerbspensumContainer with the specified ID ")
	@Nullable
	@GET
	@Path("/{erwerbspensumContID}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxErwerbspensumContainer findErwerbspensum(
		@Nonnull @NotNull @PathParam("erwerbspensumContID") JaxId erwerbspensumContID) throws EbeguRuntimeException {

		Validate.notNull(erwerbspensumContID.getId());
		String entityID = converter.toEntityId(erwerbspensumContID);
		Optional<ErwerbspensumContainer> optional = erwerbspensumService.findErwerbspensum(entityID);

		if (!optional.isPresent()) {
			return null;
		}
		ErwerbspensumContainer erwerbspenCont = optional.get();
		return converter.erwerbspensumContainerToJAX(erwerbspenCont);
	}

	@ApiOperation(value = "Returns all the ErwerbspensumContainer for the Gesuchsteller with the specified ID")
	@Nullable
	@GET
	@Path("/gesuchsteller/{gesuchstellerID}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<JaxErwerbspensumContainer> findErwerbspensumForGesuchsteller(
		@Nonnull @NotNull @PathParam("gesuchstellerID") JaxId gesuchstellerID) throws EbeguRuntimeException {

		Validate.notNull(gesuchstellerID.getId());
		String gesEntityID = converter.toEntityId(gesuchstellerID);
		Optional<Gesuchsteller> gesuchsteller = gesuchstellerService.findGesuchsteller(gesEntityID);
		Gesuchsteller gs = gesuchsteller.orElseThrow(
			() -> new EbeguEntityNotFoundException("findErwerbspensumForGesuchsteller", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchstellerId invalid: " + gesEntityID));
		Collection<ErwerbspensumContainer> pensen = erwerbspensumService.findErwerbspensenForGesuchsteller(gs);
		List<JaxErwerbspensumContainer> erwerbspensenList = pensen.stream()
			.map(erwerbspensumContainer -> converter.erwerbspensumContainerToJAX(erwerbspensumContainer))
			.collect(Collectors.toList());
		return erwerbspensenList;
	}

	@Nullable
	@DELETE
	@Path("/{erwerbspensumContID}")
	@Consumes(MediaType.WILDCARD)
	public Response removeErwerbspensum(
		@Nonnull @NotNull @PathParam("erwerbspensumContID") JaxId erwerbspensumContIDJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(erwerbspensumContIDJAXPId.getId());
		erwerbspensumService.removeErwerbspensum(converter.toEntityId(erwerbspensumContIDJAXPId));
		return Response.ok().build();
	}


}
