package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.*;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.InstitutionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

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
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * Resource fuer Gesuch
 */
@Path("gesuche")
@Stateless
@Api
public class GesuchResource {

	@Inject
	private GesuchService gesuchService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private BenutzerService benutzerService;

	@Inject
	private Principal principal;

	@Inject
	private JaxBConverter converter;

	@ApiOperation(value = "Creates a new Gesuch in the database. The transfer object also has a relation to Familiensituation " +
		"which is stored in the database as well.")
	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
		@Nonnull @NotNull JaxGesuch gesuchJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Gesuch convertedGesuch = converter.gesuchToEntity(gesuchJAXP, new Gesuch());
		Gesuch persistedGesuch = this.gesuchService.createGesuch(convertedGesuch);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(GesuchResource.class)
			.path("/" + persistedGesuch.getId())
			.build();

		JaxGesuch jaxGesuch = converter.gesuchToJAX(persistedGesuch);

		return Response.created(uri).entity(jaxGesuch).build();
	}

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuch update(
		@Nonnull @NotNull JaxGesuch gesuchJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(gesuchJAXP.getId());
		Optional<Gesuch> optGesuch = gesuchService.findGesuch(gesuchJAXP.getId());
		Gesuch gesuchFromDB = optGesuch.orElseThrow(() -> new EbeguEntityNotFoundException("update", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchJAXP.getId()));

		Gesuch gesuchToMerge = converter.gesuchToEntity(gesuchJAXP, gesuchFromDB);
		Gesuch modifiedGesuch = this.gesuchService.updateGesuch(gesuchToMerge);

		return converter.gesuchToJAX(modifiedGesuch);
	}

	@Nullable
	@GET
	@Path("/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuch findGesuch(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) throws EbeguException {
		Validate.notNull(gesuchJAXPId.getId());
		String gesuchID = converter.toEntityId(gesuchJAXPId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchID);

		if (!gesuchOptional.isPresent()) {
			return null;
		}
		Gesuch gesuchToReturn = gesuchOptional.get();
		return converter.gesuchToJAX(gesuchToReturn);
	}

	/**
	 * Methode findGesuch fuer Benutzer mit Rolle SACHBEARBEITER_INSTITUTION oder SACHBEARBEITER_TRAEGERSCHAFT. Das ganze Gesuch wird gefilter
	 * sodass nur die relevanten Daten zum Client geschickt werden.
	 *
	 * @param gesuchJAXPId ID des Gesuchs
	 * @return filtriertes Gesuch mit nur den relevanten Daten
	 * @throws EbeguException
	 */
	@Nullable
	@GET
	@Path("/institution/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuch findGesuchForInstitution(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) throws EbeguException {

		final JaxGesuch completeGesuch = findGesuch(gesuchJAXPId);

		final Optional<Benutzer> optBenutzer = benutzerService.findBenutzer(this.principal.getName());
		if (optBenutzer.isPresent()) {
			final Benutzer benutzer = optBenutzer.get();
			if (benutzer.getRole() == UserRole.SACHBEARBEITER_INSTITUTION && benutzer.getInstitution() != null) {
				final Collection<Institution> userInstitutionen = new ArrayList<>();
				userInstitutionen.add(benutzer.getInstitution());
				return cleanGesuchForInstitutionTraegerschaft(completeGesuch, userInstitutionen);
			}
			else if (benutzer.getRole() == UserRole.SACHBEARBEITER_TRAEGERSCHAFT && benutzer.getTraegerschaft() != null) {
				final Collection<Institution> userInstitutionen = institutionService.getAllInstitutionenFromTraegerschaft(benutzer.getTraegerschaft().getId());
				return cleanGesuchForInstitutionTraegerschaft(completeGesuch, userInstitutionen);
			}
			else { // fuer falsche Rolle oder keinen verknuepften Dantensatz null wird zurueckgegeben
				return null;
			}
		}
		return null;
	}

	/**
	 * Nimmt das uebergebene Gesuch und entfernt alle Daten die fuer die Rollen SACHBEARBEITER_INSTITUTION oder SACHBEARBEITER_TRAEGERSCHAFT nicht
	 * relevant sind. Dieses Gesuch wird zurueckgeliefert
	 * @param completeGesuch
	 * @param userInstitutionen
	 * @return
	 */
	private JaxGesuch cleanGesuchForInstitutionTraegerschaft(final JaxGesuch completeGesuch, final Collection<Institution> userInstitutionen) {
		//clean EKV
		completeGesuch.setEinkommensverschlechterungInfo(null);

		//clean GS -> FinSit
		if (completeGesuch.getGesuchsteller1() != null) {
			completeGesuch.getGesuchsteller1().setEinkommensverschlechterungContainer(null);
			completeGesuch.getGesuchsteller1().setErwerbspensenContainers(null);
			completeGesuch.getGesuchsteller1().setFinanzielleSituationContainer(null);
		}
		if (completeGesuch.getGesuchsteller2() != null) {
			completeGesuch.getGesuchsteller2().setEinkommensverschlechterungContainer(null);
			completeGesuch.getGesuchsteller2().setErwerbspensenContainers(null);
			completeGesuch.getGesuchsteller2().setFinanzielleSituationContainer(null);
		}

		//clean Kinds and Betreuungen
		final Iterator<JaxKindContainer> kindsIterator = completeGesuch.getKindContainers().iterator();
		while (kindsIterator.hasNext()) {
			final JaxKindContainer kind = kindsIterator.next();
			final Iterator<JaxBetreuung> betreuungIterator = kind.getBetreuungen().iterator();
			while (betreuungIterator.hasNext()) {
				final JaxBetreuung betruung = betreuungIterator.next();
				if (!isInstitutionInList(userInstitutionen, betruung.getInstitutionStammdaten().getInstitution())) {
					betreuungIterator.remove();
				}
			}
			if (kind.getBetreuungen().size() == 0) {
				kindsIterator.remove();
			}
		}
		return completeGesuch;
	}

	private boolean isInstitutionInList(Collection<Institution> userInstitutionen, JaxInstitution institutionToLookFor) {
		for (final Institution institutionInList : userInstitutionen) {
			if (institutionInList.getId() == institutionToLookFor.getId()) {
				return true;
			}
		}
		return false;
	}


}
