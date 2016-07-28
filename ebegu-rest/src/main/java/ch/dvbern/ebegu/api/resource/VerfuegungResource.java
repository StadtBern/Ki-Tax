package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.FinanzielleSituationService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.VerfuegungService;
import io.swagger.annotations.Api;

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
import java.util.Optional;

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

	@Nullable
	@POST
	@Path("/calculate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response calculateVerfuegung (
		@Nonnull @NotNull @Valid JaxGesuch gesuchJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Gesuch gesuch = converter.gesuchToStoreableEntity(gesuchJAXP);
		finanzielleSituationService.calculateFinanzDaten(gesuch);
		Gesuch gesuchWithCalcVerfuegung = verfuegungService.calculateVerfuegung(gesuch);
		// Wir wollen nur neu berechnen. Das Gesuch soll auf keinen Fall neu gespeichert werden solange die Verfuegung nicht definitiv ist
		context.setRollbackOnly();
		return Response.ok(gesuchWithCalcVerfuegung).build();
	}

	@Nullable
	@GET
	@Path("/calculate/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response calculateVerfuegung (
		@Nonnull @NotNull @PathParam ("gesuchId") JaxId gesuchstellerId,
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
		context.setRollbackOnly();
		return Response.ok(gesuchWithCalcVerfuegung).build();
	}


//	@Nullable
//	@GET
//	@Path("/{finanzielleSituationId}")
//	@Consumes(MediaType.WILDCARD)
//	@Produces(MediaType.APPLICATION_JSON)
//	public JaxFinanzielleSituationContainer findFinanzielleSituation (
//		@Nonnull @NotNull @PathParam("finanzielleSituationId") JaxId finanzielleSituationId) throws EbeguException {
//
//		Validate.notNull(finanzielleSituationId.getId());
//		String finanzielleSituationID = converter.toEntityId(finanzielleSituationId);
//		Optional<FinanzielleSituationContainer> optional = verfuegungService.findFinanzielleSituation(finanzielleSituationID);
//
//		if (!optional.isPresent()) {
//			return null;
//		}
//		FinanzielleSituationContainer finanzielleSituationToReturn = optional.get();
//		return converter.finanzielleSituationContainerToJAX(finanzielleSituationToReturn);
//	}
}
