package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxEinkommensverschlechterungContainer;
import ch.dvbern.ebegu.api.dtos.JaxFinanzModel;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.EinkommensverschlechterungService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchstellerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
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
@Path("einkommensverschlechterung")
@Stateless
@Api
public class EinkommensverschlechterungResource {

	@Inject
	private EinkommensverschlechterungService einkVerschlService;

	@Inject
	private GesuchstellerService gesuchstellerService;
	@Inject
	private GesuchService gesuchService;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private JaxBConverter converter;

	@Resource
	private EJBContext context;    //fuer rollback


	@ApiOperation(value = "Create a new EinkommensverschlechterungContainer in the database. The transfer object also has a relation to EinkommensverschlechterungContainer, " +
		"it is stored in the database as well.")
	@Nullable
	@PUT
	@Path("/{gesuchstellerId}/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveEinkommensverschlechterungContainer(
		@Nonnull @NotNull @PathParam ("gesuchId") JaxId gesuchJAXPId,
		@Nonnull @NotNull @PathParam("gesuchstellerId") JaxId gesuchstellerId,
		@Nonnull @NotNull @Valid JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchJAXPId.getId());
		if (gesuch.isPresent()) {
			Optional<GesuchstellerContainer> gesuchsteller = gesuchstellerService.findGesuchsteller(gesuchstellerId.getId());
			if (gesuchsteller.isPresent()) {
				EinkommensverschlechterungContainer convertedFinSitCont = converter.einkommensverschlechterungContainerToStorableEntity(einkommensverschlechterungContainerJAXP);
				convertedFinSitCont.setGesuchsteller(gesuchsteller.get());
				EinkommensverschlechterungContainer persistedEinkommensverschlechterungContainer =
					einkVerschlService.saveEinkommensverschlechterungContainer(convertedFinSitCont);

				URI uri = uriInfo.getBaseUriBuilder()
					.path(EinkommensverschlechterungResource.class)
					.path("/" + persistedEinkommensverschlechterungContainer.getId())
					.build();

				JaxEinkommensverschlechterungContainer jaxEinkommensverschlechterungContainer = converter.einkommensverschlechterungContainerToJAX(persistedEinkommensverschlechterungContainer);
				return Response.created(uri).entity(jaxEinkommensverschlechterungContainer).build();
			}
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
		Optional<EinkommensverschlechterungContainer> optional = einkVerschlService.findEinkommensverschlechterungContainer(einkommensverschlechterungContainerID);

		if (!optional.isPresent()) {
			return null;
		}
		EinkommensverschlechterungContainer einkommensverschlechterungContainerToReturn = optional.get();
		return converter.einkommensverschlechterungContainerToJAX(einkommensverschlechterungContainerToReturn);
	}

	@Nullable
	@POST
	@Path("/calculate/{basisJahrPlusID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response calculateEinkommensverschlechterung (
		@Nonnull @NotNull @PathParam("basisJahrPlusID") JaxId basisJahrPlusID,
		@Nonnull @NotNull @Valid JaxGesuch gesuchJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(basisJahrPlusID.getId());
		int basisJahrPlus = Integer.parseInt(converter.toEntityId(basisJahrPlusID));

		Gesuch gesuch = converter.gesuchToStoreableEntity(gesuchJAXP);
		FinanzielleSituationResultateDTO abstFinSitResultateDTO = einkVerschlService.calculateResultate(gesuch, basisJahrPlus);
		// Wir wollen nur neu berechnen. Das Gesuch soll auf keinen Fall neu gespeichert werden
		context.setRollbackOnly();
		return Response.ok(abstFinSitResultateDTO).build();
	}

	/**
	 * Diese Methode ist aehnlich wie {@link this.calculateEinkommensverschlechterung}
	 * Hier wird die  Finanzielle Situation als eigenes Model uebergeben statt das ganzes Gesuch
	 */
	@Nullable
	@POST
	@Path("/calculateTemp/{basisJahrPlusID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response calculateEinkommensverschlechterungTemp (
		@Nonnull @NotNull @PathParam("basisJahrPlusID") JaxId basisJahrPlusID,
		@Nonnull @NotNull @Valid JaxFinanzModel jaxFinSitModel,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(basisJahrPlusID.getId());
		int basisJahrPlus = Integer.parseInt(converter.toEntityId(basisJahrPlusID));
		Gesuch gesuch = new Gesuch();
		gesuch.setFamiliensituation(new Familiensituation());
		gesuch.getFamiliensituation().setGemeinsameSteuererklaerung(jaxFinSitModel.isGemeinsameSteuererklaerung());
		if (jaxFinSitModel.getFinanzielleSituationContainerGS1() != null) {
			gesuch.setGesuchsteller1(new GesuchstellerContainer());
			gesuch.getGesuchsteller1().setGesuchstellerJA(new Gesuchsteller());
			gesuch.getGesuchsteller1().setFinanzielleSituationContainer(
				converter.finanzielleSituationContainerToStorableEntity(jaxFinSitModel.getFinanzielleSituationContainerGS1()));
		}
		if (jaxFinSitModel.getFinanzielleSituationContainerGS2() != null) {
			gesuch.setGesuchsteller2(new GesuchstellerContainer());
			gesuch.getGesuchsteller2().setGesuchstellerJA(new Gesuchsteller());
			gesuch.getGesuchsteller2().setFinanzielleSituationContainer(
				converter.finanzielleSituationContainerToStorableEntity(jaxFinSitModel.getFinanzielleSituationContainerGS2()));
		}
		if (jaxFinSitModel.getEinkommensverschlechterungContainerGS1() != null) {
			if(gesuch.getGesuchsteller1() ==null) {
				gesuch.setGesuchsteller1(new GesuchstellerContainer());
				gesuch.getGesuchsteller1().setGesuchstellerJA(new Gesuchsteller());
			}
			gesuch.getGesuchsteller1().setEinkommensverschlechterungContainer(
				converter.einkommensverschlechterungContainerToStorableEntity(jaxFinSitModel.getEinkommensverschlechterungContainerGS1()));
		}
		if (jaxFinSitModel.getEinkommensverschlechterungContainerGS2() != null) {
			if(gesuch.getGesuchsteller2() ==null) {
				gesuch.setGesuchsteller2(new GesuchstellerContainer());
				gesuch.getGesuchsteller2().setGesuchstellerJA(new Gesuchsteller());
			}
			gesuch.getGesuchsteller2().setEinkommensverschlechterungContainer(
				converter.einkommensverschlechterungContainerToStorableEntity(jaxFinSitModel.getEinkommensverschlechterungContainerGS2()));
		}
		if (jaxFinSitModel.getEinkommensverschlechterungInfo() != null) {
			gesuch.setEinkommensverschlechterungInfo(
				converter.einkommensverschlechterungInfoToEntity(jaxFinSitModel.getEinkommensverschlechterungInfo(), new EinkommensverschlechterungInfo()));
		}

		FinanzielleSituationResultateDTO abstFinSitResultateDTO = einkVerschlService.calculateResultate(gesuch, basisJahrPlus);
		// Wir wollen nur neu berechnen. Das Gesuch soll auf keinen Fall neu gespeichert werden
		context.setRollbackOnly();
		return Response.ok(abstFinSitResultateDTO).build();
	}
}
