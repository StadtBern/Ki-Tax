package ch.dvbern.ebegu.api.resource;

import java.net.URI;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxEinkommensverschlechterungContainer;
import ch.dvbern.ebegu.api.dtos.JaxFinanzModel;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.util.ResourceHelper;
import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.entities.FinanzielleSituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.EinkommensverschlechterungService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchstellerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

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

	@Inject
	private ResourceHelper resourceHelper;


	@ApiOperation("Create a new EinkommensverschlechterungContainer in the database. The transfer object also has a relation to "
		+ "EinkommensverschlechterungContainer, it is stored in the database as well.")
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

		Gesuch gesuch = gesuchService.findGesuch(gesuchJAXPId.getId()).orElseThrow(() -> new EbeguEntityNotFoundException("saveEinkommensverschlechterungContainer", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchJAXPId.getId()));
		// Sicherstellen, dass das dazugehoerige Gesuch ueberhaupt noch editiert werden darf fuer meine Rolle
		resourceHelper.assertGesuchStatusForBenutzerRole (gesuch);
			GesuchstellerContainer gesuchsteller = gesuchstellerService.findGesuchsteller(gesuchstellerId.getId()).orElseThrow(() -> new EbeguEntityNotFoundException("saveEinkommensverschlechterungContainer", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchstellerId invalid: " + gesuchstellerId.getId()) );
				EinkommensverschlechterungContainer convertedEKVCont = converter.einkommensverschlechterungContainerToStorableEntity(einkommensverschlechterungContainerJAXP);
				convertedEKVCont.setGesuchsteller(gesuchsteller);
				EinkommensverschlechterungContainer persistedEinkommensverschlechterungContainer =
					einkVerschlService.saveEinkommensverschlechterungContainer(convertedEKVCont, gesuch.getId());

		URI uri = uriInfo.getBaseUriBuilder()
			.path(EinkommensverschlechterungResource.class)
			.path('/' + persistedEinkommensverschlechterungContainer.getId())
			.build();

		JaxEinkommensverschlechterungContainer jaxEinkommensverschlechterungContainer = converter.einkommensverschlechterungContainerToJAX(persistedEinkommensverschlechterungContainer);
		return Response.created(uri).entity(jaxEinkommensverschlechterungContainer).build();
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
	@GET
	@Path("/forGesuchsteller/{gesuchstellerId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxEinkommensverschlechterungContainer findEkvContainerForGesuchsteller(
		@Nonnull @NotNull @PathParam("gesuchstellerId") JaxId gesuchstellerId) throws EbeguException {

		Validate.notNull(gesuchstellerId.getId());
		String gsID = converter.toEntityId(gesuchstellerId);
		Optional<GesuchstellerContainer> optionalGS = gesuchstellerService.findGesuchsteller(gsID);
		if (!optionalGS.isPresent()) {
			throw new EbeguEntityNotFoundException("findEkvContainerForGesuchsteller", ErrorCodeEnum
				.ERROR_ENTITY_NOT_FOUND, "GesuchstellerId not found: " + gesuchstellerId.getId());
		}
		GesuchstellerContainer gsContainer = optionalGS.get();
		return converter.einkommensverschlechterungContainerToJAX(gsContainer.getEinkommensverschlechterungContainer());
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
		gesuch.initFamiliensituationContainer();
		gesuch.extractFamiliensituation().setGemeinsameSteuererklaerung(jaxFinSitModel.isGemeinsameSteuererklaerung());
		if (jaxFinSitModel.getFinanzielleSituationContainerGS1() != null) {
			gesuch.setGesuchsteller1(new GesuchstellerContainer());
			gesuch.getGesuchsteller1().setGesuchstellerJA(new Gesuchsteller());
			gesuch.getGesuchsteller1().setFinanzielleSituationContainer(
				converter.finanzielleSituationContainerToEntity(jaxFinSitModel.getFinanzielleSituationContainerGS1(), new FinanzielleSituationContainer()));
		}
		if (jaxFinSitModel.getFinanzielleSituationContainerGS2() != null) {
			gesuch.setGesuchsteller2(new GesuchstellerContainer());
			gesuch.getGesuchsteller2().setGesuchstellerJA(new Gesuchsteller());
			gesuch.getGesuchsteller2().setFinanzielleSituationContainer(
				converter.finanzielleSituationContainerToEntity(jaxFinSitModel.getFinanzielleSituationContainerGS2(), new FinanzielleSituationContainer()));
		}
		if (jaxFinSitModel.getEinkommensverschlechterungContainerGS1() != null) {
			if(gesuch.getGesuchsteller1() ==null) {
				gesuch.setGesuchsteller1(new GesuchstellerContainer());
				gesuch.getGesuchsteller1().setGesuchstellerJA(new Gesuchsteller());
			}
			gesuch.getGesuchsteller1().setEinkommensverschlechterungContainer(
				converter.einkommensverschlechterungContainerToEntity(jaxFinSitModel.getEinkommensverschlechterungContainerGS1(), new EinkommensverschlechterungContainer()));
		}
		if (jaxFinSitModel.getEinkommensverschlechterungContainerGS2() != null) {
			if(gesuch.getGesuchsteller2() ==null) {
				gesuch.setGesuchsteller2(new GesuchstellerContainer());
				gesuch.getGesuchsteller2().setGesuchstellerJA(new Gesuchsteller());
			}
			gesuch.getGesuchsteller2().setEinkommensverschlechterungContainer(
				converter.einkommensverschlechterungContainerToEntity(jaxFinSitModel.getEinkommensverschlechterungContainerGS2(), new EinkommensverschlechterungContainer()));
		}
		if (jaxFinSitModel.getEinkommensverschlechterungInfoContainer() != null) {
			gesuch.setEinkommensverschlechterungInfoContainer(
				converter.einkommensverschlechterungInfoContainerToEntity(jaxFinSitModel.getEinkommensverschlechterungInfoContainer(), new EinkommensverschlechterungInfoContainer()));
		}

		FinanzielleSituationResultateDTO abstFinSitResultateDTO = einkVerschlService.calculateResultate(gesuch, basisJahrPlus);
		// Wir wollen nur neu berechnen. Das Gesuch soll auf keinen Fall neu gespeichert werden
		context.setRollbackOnly();
		return Response.ok(abstFinSitResultateDTO).build();
	}
}
