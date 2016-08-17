package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxKindContainer;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.FinanzielleSituationService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.VerfuegungService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * REST Resource fuer FinanzielleSituation
 */
@Path("verfuegung")
@Stateless
@Api
public class VerfuegungResource {

	@Inject
	private VerfuegungService verfuegungService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private FinanzielleSituationService finanzielleSituationService;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private JaxBConverter converter;

	@Resource
	private EJBContext context;    //fuer rollback

//
//	@ApiOperation(value = "Create a new FinanzielleSituation in the database. The transfer object also has a relation to FinanzielleSituation, " +
//		"it is stored in the database as well.")
//	@Nullable
//	@PUT
//	@Path("/{gesuchstellerId}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response saveFinanzielleSituation (
//		@Nonnull @NotNull @PathParam ("gesuchstellerId") JaxId gesuchstellerId,
//		@Nonnull @NotNull @Valid JaxFinanzielleSituationContainer finanzielleSituationJAXP,
//		@Context UriInfo uriInfo,
//		@Context HttpServletResponse response) throws EbeguException {
//
//		Optional<Gesuchsteller> gesuchsteller = gesuchstellerService.findGesuchsteller(gesuchstellerId.getId());
//		if (gesuchsteller.isPresent()) {
//			FinanzielleSituationContainer convertedFinSitCont = converter.finanzielleSituationContainerToStorableEntity(finanzielleSituationJAXP);
//			convertedFinSitCont.setGesuchsteller(gesuchsteller.get());
//			FinanzielleSituationContainer persistedFinanzielleSituation = this.verfuegungService.saveFinanzielleSituation(convertedFinSitCont);
//
//			URI uri = uriInfo.getBaseUriBuilder()
//				.path(VerfuegungResource.class)
//				.path("/" + persistedFinanzielleSituation.getId())
//				.build();
//
//			JaxFinanzielleSituationContainer jaxFinanzielleSituation = converter.finanzielleSituationContainerToJAX(persistedFinanzielleSituation);
//			return Response.created(uri).entity(jaxFinanzielleSituation).build();
//		}
//		throw new EbeguEntityNotFoundException("saveFinanzielleSituation", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchstellerId invalid: " + gesuchstellerId.getId());
//	}


	@ApiOperation(value = "Calculates the Verfuegung of the Gesuch with the given id, does nothing if the Gesuch does not exists. Note: Nothing is stored in the Databse")
	@Nullable
	@GET
	@Path("/calculate/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response calculateVerfuegung(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchstellerId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchstellerId.getId());

		if (!gesuchOptional.isPresent()) {
			return null;
		}
		Gesuch gesuch = gesuchOptional.get();
		this.finanzielleSituationService.calculateFinanzDaten(gesuch);
		Gesuch gesuchWithCalcVerfuegung = verfuegungService.calculateVerfuegung(gesuch);
		// Wir wollen nur neu berechnen. Das Gesuch soll auf keinen Fall neu gespeichert werden solange die Verfuegung nicht definitiv ist
		JaxGesuch gesuchJax = converter.gesuchToJAX(gesuchWithCalcVerfuegung);

		Collection<Institution> instForCurrBenutzer = institutionService.getInstitutionenForCurrentBenutzer();

		// Wir verwenden das Gesuch nur zur Berechnung und wollen nicht speichern, darum Transaktion rollbacken
		context.setRollbackOnly();

		Set<JaxKindContainer> kindContainers = gesuchJax.getKindContainers();
		if (instForCurrBenutzer.size() > 0) {
			RestUtil.purgeKinderAndBetreuungenOfInstitutionen(kindContainers, instForCurrBenutzer);
		}

		return Response.ok(kindContainers).build();
	}
}

