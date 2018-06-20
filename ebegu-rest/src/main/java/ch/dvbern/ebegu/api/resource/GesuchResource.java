/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.api.resource;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAntragSearchresultDTO;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.util.ResourceHelper;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.dto.JaxAntragDTO;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.FinSitStatus;
import ch.dvbern.ebegu.enums.GesuchBetreuungenStatus;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.util.AntragStatusConverterUtil;
import ch.dvbern.ebegu.util.DateUtil;
import com.google.common.collect.ArrayListMultimap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;
/**
 * Resource fuer Gesuch
 */
@Path("gesuche")
@Stateless
@Api(description = "Resource für Anträge (Erstgesuch oder Mutation)")
public class GesuchResource {

	public static final String GESUCH_ID_INVALID = "GesuchId invalid: ";

	@Inject
	private GesuchService gesuchService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private FallService fallService;

	@Inject
	private PrincipalBean principalBean;

	@Inject
	private JaxBConverter converter;

	@Inject
	private ResourceHelper resourceHelper;

	@ApiOperation(value = "Creates a new Antrag in the database. The transfer object also has a relation to " +
		"Familiensituation which is stored in the database as well.", response = JaxGesuch.class)
	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
		@Nonnull @NotNull JaxGesuch gesuchJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Gesuch convertedGesuch = converter.gesuchToEntity(gesuchJAXP, new Gesuch());
		Gesuch persistedGesuch = this.gesuchService.createGesuch(convertedGesuch);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(GesuchResource.class)
			.path('/' + persistedGesuch.getId())
			.build();

		JaxGesuch jaxGesuch = converter.gesuchToJAX(persistedGesuch);
		return Response.created(uri).entity(jaxGesuch).build();
	}

	@ApiOperation(value = "Updates a Antrag in the database", response = JaxGesuch.class)
	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuch update(
		@Nonnull @NotNull JaxGesuch gesuchJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(gesuchJAXP.getId());
		Optional<Gesuch> optGesuch = gesuchService.findGesuch(gesuchJAXP.getId());

		Gesuch gesuchFromDB = optGesuch.orElseThrow(() -> new EbeguEntityNotFoundException("update", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchJAXP.getId()));
		//only if status has changed: Muss ermittelt werden, BEVOR wir mergen!
		final boolean saveInStatusHistory = gesuchFromDB.getStatus() != AntragStatusConverterUtil.convertStatusToEntity(gesuchJAXP.getStatus());
		Gesuch gesuchToMerge = converter.gesuchToEntity(gesuchJAXP, gesuchFromDB);
		Gesuch modifiedGesuch = this.gesuchService.updateGesuch(gesuchToMerge, saveInStatusHistory, null);
		return converter.gesuchToJAX(modifiedGesuch);
	}

	@ApiOperation(value = "Gibt den Antrag mit der uebergebenen Id zurueck. Dabei wird geprueft, ob der eingeloggte " +
		"Benutzer ueberhaupt fuer das Gesuch berechtigt ist.", response = JaxGesuch.class)
	@Nullable
	@GET
	@Path("/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuch findGesuch(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) {
		Validate.notNull(gesuchJAXPId.getId());
		String gesuchID = converter.toEntityId(gesuchJAXPId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchID);

		if (!gesuchOptional.isPresent()) {
			return null;
		}
		Gesuch gesuchToReturn = gesuchOptional.get();
		final JaxGesuch jaxGesuch = converter.gesuchToJAX(gesuchToReturn);
		return jaxGesuch;
	}

	/**
	 * Da beim Einscannen Gesuche eingelesen werden die noch im Status Freigabequittung sind brauchen
	 * wir hier eine separate Methode um das Lesen der noetigen Informationen dieser Gesuche zuzulassen
	 * Wenn kein Gesuch gefunden wird wird null zurueckgegeben.
	 *
	 * @param gesuchJAXPId gesuchID des Gesuchs im Status Freigabequittung oder hoeher
	 * @return DTO mit den relevanten Informationen zum Gesuch
	 */
	@ApiOperation(value = "Gibt den Antrag mit der uebergebenen Id zurueck. Da beim Einscannen Gesuche eingelesen " +
		"werden die noch im Status Freigabequittung sind brauchen wir hier eine separate Methode um das Lesen der " +
		"noetigen Informationen dieser Gesuche zuzulassen.", response = JaxGesuch.class)
	@Nullable
	@GET
	@Path("/freigabe/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxAntragDTO findGesuchForFreigabe(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) {
		Validate.notNull(gesuchJAXPId.getId());
		String gesuchID = converter.toEntityId(gesuchJAXPId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuchForFreigabe(gesuchID);

		if (!gesuchOptional.isPresent()) {
			return null;
		}
		Gesuch gesuchToReturn = gesuchOptional.get();
		JaxAntragDTO jaxAntragDTO = converter.gesuchToAntragDTO(gesuchToReturn, principalBean.discoverMostPrivilegedRole());
		jaxAntragDTO.setFamilienName(gesuchToReturn.extractFullnamesString()); //hier volle Namen beider GS
		return jaxAntragDTO;
	}

	/**
	 * Methode findGesuch fuer Benutzer mit Rolle SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT oder
	 * SCHULAMT / SCHULAMT_ADMIN. Das ganze Gesuch wird gefilter
	 * sodass nur die relevanten Daten zum Client geschickt werden.
	 *
	 * @param gesuchJAXPId ID des Gesuchs
	 * @return filtriertes Gesuch mit nur den relevanten Daten
	 */
	@ApiOperation(value = "Gibt den Antrag mit der uebergebenen Id zurueck. Methode fuer Benutzer mit Rolle " +
		"SACHBEARBEITER_INSTITUTION oder SACHBEARBEITER_TRAEGERSCHAFT. Das ganze Gesuch wird gefiltert so dass nur " +
		"die relevanten Daten zum Client geschickt werden.", response = JaxGesuch.class)
	@Nullable
	@GET
	@Path("/institution/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuch findGesuchForInstitution(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) {

		final JaxGesuch completeGesuch = findGesuch(gesuchJAXPId);

		UserRole role = principalBean.discoverMostPrivilegedRole();
		if (role != null) {
			if (UserRole.SUPER_ADMIN == role) {
				return completeGesuch;
			} else {
				Collection<Institution> instForCurrBenutzer = institutionService.getAllowedInstitutionenForCurrentBenutzer(false);
				return cleanGesuchForInstitutionTraegerschaft(completeGesuch, instForCurrBenutzer);
			}
		}
		return null; // aus sicherheitsgruenden geben wir null zurueck wenn etwas nicht stimmmt
	}

	/**
	 * Nimmt das uebergebene Gesuch und entfernt alle Daten die fuer die Rollen SACHBEARBEITER_INSTITUTION oder SACHBEARBEITER_TRAEGERSCHAFT nicht
	 * relevant sind. Dieses Gesuch wird zurueckgeliefert
	 */
	private JaxGesuch cleanGesuchForInstitutionTraegerschaft(final JaxGesuch completeGesuch, final Collection<Institution> userInstitutionen) {
		//clean EKV
		completeGesuch.setEinkommensverschlechterungInfoContainer(null);

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

		RestUtil.purgeKinderAndBetreuungenOfInstitutionen(completeGesuch.getKindContainers(), userInstitutionen);
		return completeGesuch;
	}

	@ApiOperation(value = "Aktualisiert die Bemerkungen fuer ein Gesuch.", response = Void.class)
	@Nullable
	@PUT
	@Path("/bemerkung/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateBemerkung(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId,
		@Nonnull @NotNull String bemerkung,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(gesuchJAXPId.getId());
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(converter.toEntityId(gesuchJAXPId));

		if (gesuchOptional.isPresent()) {
			gesuchOptional.get().setBemerkungen(bemerkung);

			gesuchService.updateGesuch(gesuchOptional.get(), false, null);

			return Response.ok().build();
		}
		throw new EbeguEntityNotFoundException("updateBemerkung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + gesuchJAXPId.getId());
	}

	@ApiOperation(value = "Aktualisiert die Bemerkungen der Steuerverwaltung fuer ein Gesuch.", response = Void.class)
	@Nullable
	@PUT
	@Path("/bemerkungPruefungSTV/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateBemerkungPruefungSTV(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId,
		@Nonnull @NotNull String bemerkungPruefungSTV,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(gesuchJAXPId.getId());
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(converter.toEntityId(gesuchJAXPId));

		if (gesuchOptional.isPresent()) {
			gesuchOptional.get().setBemerkungenPruefungSTV(bemerkungPruefungSTV);

			gesuchService.updateGesuch(gesuchOptional.get(), false, null);

			return Response.ok().build();
		}
		throw new EbeguEntityNotFoundException("updateBemerkungPruefungSTV", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + gesuchJAXPId.getId());
	}

	@ApiOperation(value = "Aktualisiert den Status eines Gesuchs", response = Void.class)
	@Nullable
	@PUT
	@Path("/status/{gesuchId}/{statusDTO}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStatus(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId,
		@Nonnull @NotNull @PathParam("statusDTO") AntragStatusDTO statusDTO) {

		Validate.notNull(gesuchJAXPId.getId());
		Validate.notNull(statusDTO);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(converter.toEntityId(gesuchJAXPId));

		if (gesuchOptional.isPresent()) {
			if (gesuchOptional.get().getStatus() != AntragStatusConverterUtil.convertStatusToEntity(statusDTO)) {
				//only if status has changed
				gesuchOptional.get().setStatus(AntragStatusConverterUtil.convertStatusToEntity(statusDTO));
				gesuchService.updateGesuch(gesuchOptional.get(), true, null);
			}
			return Response.ok().build();
		}
		String message = "Could not update Status because the Geusch with ID " + gesuchJAXPId.getId() + " could not be read";
		throw new EbeguEntityNotFoundException("updateStatus", message, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + gesuchJAXPId.getId());
	}

	/**
	 * iteriert durch eine Liste von Antragen und gibt jeweils pro Fall nur den Antrag mit dem neusten Eingangsdatum zurueck
	 *
	 * @param foundAntraege Liste mit Antraegen, kann mehrere pro Fall enthalten
	 * @return Set mit Antraegen, jeweils nur der neuste zu einem bestimmten Fall
	 */
	@Nonnull
	@SuppressWarnings(value = { "unused" })
	@SuppressFBWarnings(value = "UPM_UNCALLED_PRIVATE_METHOD")
	private Set<Gesuch> reduceToNewestAntrag(List<Gesuch> foundAntraege) {
		ArrayListMultimap<Fall, Gesuch> fallToAntragMultimap = ArrayListMultimap.create();
		for (Gesuch gesuch : foundAntraege) {
			fallToAntragMultimap.put(gesuch.getFall(), gesuch);
		}
		Set<Gesuch> gesuchSet = new LinkedHashSet<>();
		for (Gesuch gesuch : foundAntraege) {
			List<Gesuch> antraege = fallToAntragMultimap.get(gesuch.getFall());
			antraege.sort(Comparator.comparing(Gesuch::getEingangsdatum));
			gesuchSet.add(antraege.get(0)); //nur neusten zurueckgeben
		}
		return gesuchSet;
	}

	@ApiOperation(value = "Gibt alle Antraege (Gesuche und Mutationen) eines Falls zurueck",
		responseContainer = "List", response = JaxAntragDTO.class)
	@Nonnull
	@GET
	@Path("/fall/{fallId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxAntragDTO> getAllAntragDTOForFall(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallJAXPId) {
		Validate.notNull(fallJAXPId.getId());
		return gesuchService.getAllAntragDTOForFall(converter.toEntityId(fallJAXPId));
	}

	@ApiOperation(value = "Creates a new Antrag of type Mutation in the database", response = JaxGesuch.class)
	@Nullable
	@POST
	@Path("/mutieren/{antragId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response antragMutieren(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Nullable @QueryParam("date") String stringDate,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(antragJaxId.getId());

		// Wenn der GS eine Mutation macht, ist das Eingangsdatum erst null. Wir muessen das Gesuch so erstellen
		LocalDate eingangsdatum = null;
		if (stringDate != null && !stringDate.isEmpty()) {
			eingangsdatum = DateUtil.parseStringToDateOrReturnNow(stringDate);
		}
		final String antragId = converter.toEntityId(antragJaxId);

		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(antragId, eingangsdatum);

		if (!gesuchOptional.isPresent()) {
			return Response.noContent().build();
		}

		Gesuch mutationToReturn = gesuchService.createGesuch(gesuchOptional.get());
		return Response.ok(converter.gesuchToJAX(mutationToReturn)).build();
	}

	@ApiOperation(value = "Creates a new Antrag of type Erneuerungsgesuch in the database", response = JaxGesuch.class)
	@Nullable
	@POST
	@Path("/erneuern/{gesuchsperiodeId}/{antragId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response antragErneuern(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Nonnull @NotNull @PathParam("gesuchsperiodeId") JaxId gesuchsperiodeJaxId,
		@Nullable @QueryParam("date") String stringDate,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(gesuchsperiodeJaxId.getId());
		Validate.notNull(antragJaxId.getId());

		// Wenn der GS ein Erneuerungsgesuch macht, ist das Eingangsdatum erst null. Wir muessen das Gesuch so erstellen
		LocalDate eingangsdatum = null;
		if (stringDate != null && !stringDate.isEmpty()) {
			eingangsdatum = DateUtil.parseStringToDateOrReturnNow(stringDate);
		}
		final String antragId = converter.toEntityId(antragJaxId);
		final String gesuchsperiodeId = converter.toEntityId(gesuchsperiodeJaxId);

		Optional<Gesuch> gesuchsperiodeOptional = gesuchService.antragErneuern(antragId, gesuchsperiodeId, eingangsdatum);
		if (!gesuchsperiodeOptional.isPresent()) {
			return Response.noContent().build();
		}
		Gesuch gesuchToReturn = gesuchService.createGesuch(gesuchsperiodeOptional.get());
		return Response.ok(converter.gesuchToJAX(gesuchToReturn)).build();
	}

	@ApiOperation(value = "Gibt den Antrag frei und bereitet ihn vor für die Bearbeitung durch das Jugendamt",
		response = JaxGesuch.class)
	@Nullable
	@POST
	@Path("/freigeben/{antragId}/JA/{usernameJA}/SCH/{usernameSCH}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response antragFreigeben(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Nullable @PathParam("usernameJA") String usernameJA,
		@Nullable @PathParam("usernameSCH") String usernameSCH,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		// Sicherstellen, dass der Status des Client-Objektes genau dem des Servers entspricht
		resourceHelper.assertGesuchStatusForFreigabe(antragJaxId.getId());

		Validate.notNull(antragJaxId.getId());

		final String antragId = converter.toEntityId(antragJaxId);

		Gesuch gesuch = gesuchService.antragFreigeben(antragId, usernameJA, usernameSCH);
		return Response.ok(converter.gesuchToJAX(gesuch)).build();
	}

	@ApiOperation(value = "Setzt das gegebene Gesuch als Beschwerde haengig und bei allen Gesuchen der Periode das " +
		"Flag gesperrtWegenBeschwerde auf true", response = JaxGesuch.class)
	@Nullable
	@POST
	@Path("/setBeschwerde/{antragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setBeschwerdeHaengig(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(antragJaxId.getId());
		final String antragId = converter.toEntityId(antragJaxId);
		Optional<Gesuch> gesuch = gesuchService.findGesuch(antragId);

		resourceHelper.assertGesuchStatusEqual(antragId, AntragStatusDTO.VERFUEGT, AntragStatusDTO.PRUEFUNG_STV,
			AntragStatusDTO.IN_BEARBEITUNG_STV, AntragStatusDTO.GEPRUEFT_STV, AntragStatusDTO.KEIN_ANGEBOT, AntragStatusDTO.NUR_SCHULAMT);

		if (gesuch.isPresent()) {
			Gesuch persistedGesuch = gesuchService.setBeschwerdeHaengigForPeriode(gesuch.get());
			return Response.ok(converter.gesuchToJAX(persistedGesuch)).build();
		}
		throw new EbeguEntityNotFoundException("setBeschwerdeHaengig", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + antragJaxId.getId());
	}

	@ApiOperation(value = "Setzt das gegebene Gesuch als Abgeschossen (Status NUR_SCHULAMT)", response = JaxGesuch.class)
	@Nullable
	@POST
	@Path("/setAbschliessen/{antragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setAbschliessen(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(antragJaxId.getId());
		final String antragId = converter.toEntityId(antragJaxId);
		Optional<Gesuch> gesuch = gesuchService.findGesuch(antragId);

		if (gesuch.isPresent()) {
			resourceHelper.assertGesuchStatusEqual(antragId, AntragStatusDTO.IN_BEARBEITUNG_JA, AntragStatusDTO.GEPRUEFT);
			Gesuch persistedGesuch = gesuchService.setAbschliessen(gesuch.get());
			final JaxGesuch jaxGesuch = converter.gesuchToJAX(persistedGesuch);
			return Response.ok(jaxGesuch).build();
		}
		throw new EbeguEntityNotFoundException("setAbschliessen", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + antragJaxId.getId());
	}

	@ApiOperation(value = "Setzt das gegebene Gesuch als PRUEFUNG_STV", response = JaxGesuch.class)
	@Nullable
	@POST
	@Path("/sendToSTV/{antragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendGesuchToSTV(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Nullable String bemerkungen,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		// Sicherstellen, dass der Status des Client-Objektes genau dem des Servers entspricht
		resourceHelper.assertGesuchStatusEqual(antragJaxId.getId(), AntragStatusDTO.VERFUEGT, AntragStatusDTO.NUR_SCHULAMT);

		Validate.notNull(antragJaxId.getId());
		final String antragId = converter.toEntityId(antragJaxId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(antragId);

		if (!gesuchOptional.isPresent()) {
			throw new EbeguEntityNotFoundException("sendGesuchToSTV", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + antragJaxId.getId());
		}
		Gesuch gesuch = gesuchOptional.get();
		Gesuch persistedGesuch = gesuchService.sendGesuchToSTV(gesuch, bemerkungen);
		return Response.ok(converter.gesuchToJAX(persistedGesuch)).build();
	}

	@ApiOperation(value = "Setzt das gegebene Gesuch als GEPRUEFT_STV und das Flag geprueftSTV als true",
		response = JaxGesuch.class)
	@Nullable
	@POST
	@Path("/freigebenSTV/{antragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response gesuchBySTVFreigeben(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		// Sicherstellen, dass der Status des Client-Objektes genau dem des Servers entspricht
		resourceHelper.assertGesuchStatusEqual(antragJaxId.getId(), AntragStatusDTO.IN_BEARBEITUNG_STV);

		Validate.notNull(antragJaxId.getId());
		final String antragId = converter.toEntityId(antragJaxId);
		Optional<Gesuch> gesuch = gesuchService.findGesuch(antragId);

		if (!gesuch.isPresent()) {
			throw new EbeguEntityNotFoundException("gesuchBySTVFreigeben", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + antragJaxId.getId());
		}

		Gesuch persistedGesuch = gesuchService.gesuchBySTVFreigeben(gesuch.get());
		return Response.ok(converter.gesuchToJAX(persistedGesuch)).build();

	}

	@ApiOperation(value = "Setzt das gegebene Gesuch als VERFUEGT und das Flag geprueftSTV als true",
		response = JaxGesuch.class)
	@Nullable
	@POST
	@Path("/stvPruefungAbschliessen/{antragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response stvPruefungAbschliessen(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		// Sicherstellen, dass der Status des Client-Objektes genau dem des Servers entspricht
		resourceHelper.assertGesuchStatusEqual(antragJaxId.getId(), AntragStatusDTO.GEPRUEFT_STV);

		Validate.notNull(antragJaxId.getId());
		final String antragId = converter.toEntityId(antragJaxId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(antragId);

		Gesuch gesuch = gesuchOptional.orElseThrow(() -> new EbeguEntityNotFoundException("stvPruefungAbschliessen", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + antragJaxId.getId()));

		if (AntragStatus.GEPRUEFT_STV != gesuch.getStatus()) {
			// Wir vergewissern uns dass das Gesuch im Status IN_BEARBEITUNG_STV ist, da sonst kann es nicht fuer das JA freigegeben werden
			throw new EbeguRuntimeException("stvPruefungAbschliessen", ErrorCodeEnum.ERROR_ONLY_IN_GEPRUEFT_STV_ALLOWED, "Status ist: " + gesuch.getStatus());
		}

		Gesuch persistedGesuch = gesuchService.stvPruefungAbschliessen(gesuch);
		return Response.ok(converter.gesuchToJAX(persistedGesuch)).build();

	}

	@ApiOperation(value = "Setzt das gegebene Gesuch als VERFUEGT und bei allen Gescuhen der Periode den Flag " +
		"gesperrtWegenBeschwerde auf false", response = JaxGesuch.class)
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@Nullable
	@POST
	@Path("/removeBeschwerde/{antragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeBeschwerdeHaengig(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		// Sicherstellen, dass der Status des Client-Objektes genau dem des Servers entspricht
		resourceHelper.assertGesuchStatusEqual(antragJaxId.getId(), AntragStatusDTO.BESCHWERDE_HAENGIG);

		Validate.notNull(antragJaxId.getId());
		final String antragId = converter.toEntityId(antragJaxId);
		Optional<Gesuch> gesuch = gesuchService.findGesuch(antragId);

		if (gesuch.isPresent()) {
			Gesuch persistedGesuch = gesuchService.removeBeschwerdeHaengigForPeriode(gesuch.get());
			return Response.ok(converter.gesuchToJAX(persistedGesuch)).build();
		}
		throw new EbeguEntityNotFoundException("removeBeschwerdeHaengig", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + antragJaxId.getId());
	}

	@ApiOperation(value = "Loescht eine online Mutation", response = Void.class)
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@DELETE
	@Path("/removeOnlineMutation/{fallId}/{gesuchsperiodeId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeOnlineMutation(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallId,
		@Nonnull @NotNull @PathParam("gesuchsperiodeId") JaxId gesuchsperiodeId,
		@Context HttpServletResponse response) {

		Validate.notNull(fallId.getId());
		Validate.notNull(gesuchsperiodeId.getId());
		Optional<Fall> fall = fallService.findFall(fallId.getId());
		if (!fall.isPresent()) {
			throw new EbeguEntityNotFoundException("removeOnlineMutation", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "Fall_ID invalid " + fallId.getId());
		}
		Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(gesuchsperiodeId.getId());
		if (!gesuchsperiode.isPresent()) {
			throw new EbeguEntityNotFoundException("removeOnlineMutation", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "Gesuchsperiode_ID invalid " + gesuchsperiodeId.getId());
		}
		gesuchService.removeOnlineMutation(fall.get(), gesuchsperiode.get());

		return Response.ok().build();
	}

	@ApiOperation(value = "Loescht ein online Erneuerungsgesuch", response = Void.class)
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@DELETE
	@Path("/removeOnlineFolgegesuch/{fallId}/{gesuchsperiodeId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeOnlineFolgegesuch(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallJAXPId,
		@Nonnull @NotNull @PathParam("gesuchsperiodeId") JaxId gesuchsperiodeJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(fallJAXPId.getId());
		Validate.notNull(gesuchsperiodeJAXPId.getId());

		Optional<Fall> fall = fallService.findFall(fallJAXPId.getId());
		if (!fall.isPresent()) {
			throw new EbeguEntityNotFoundException("removeOnlineFolgegesuch", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "Fall_ID invalid " + fallJAXPId.getId());
		}
		Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(gesuchsperiodeJAXPId.getId());
		if (!gesuchsperiode.isPresent()) {
			throw new EbeguEntityNotFoundException("removeOnlineFolgegesuch", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchsperiodeId invalid: " + gesuchsperiodeJAXPId.getId());
		}
		gesuchService.removeOnlineFolgegesuch(fall.get(), gesuchsperiode.get());

		return Response.ok().build();
	}

	@DELETE
	@Path("/removePapiergesuch/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	public Response removePapiergesuch(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJaxId,
		@Context HttpServletResponse response) {

		Validate.notNull(gesuchJaxId.getId());

		Gesuch gesuch = gesuchService.findGesuch(gesuchJaxId.getId(), true).orElseThrow(()
			-> new EbeguEntityNotFoundException("removePapiergesuch", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchJaxId.getId()));

		gesuchService.removePapiergesuch(gesuch);

		return Response.ok().build();
	}

	@DELETE
	@Path("/removeGesuchstellerAntrag/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeGesuchstellerAntrag(@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJaxId,
		@Context HttpServletResponse response) {

		Validate.notNull(gesuchJaxId.getId());

		Gesuch gesuch = gesuchService.findGesuch(gesuchJaxId.getId(), true).orElseThrow(()
			-> new EbeguEntityNotFoundException("removeGesuchstellerAntrag", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchJaxId.getId()));

		gesuchService.removeGesuchstellerAntrag(gesuch);

		return Response.ok().build();
	}

	@ApiOperation(value = "Schliesst ein Gesuch ab, das kein Angebot hat", response = JaxGesuch.class)
	@Nullable
	@POST
	@Path("/closeWithoutAngebot/{antragId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response closeWithoutAngebot(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		// Sicherstellen, dass der Status des Client-Objektes genau dem des Servers entspricht
		resourceHelper.assertGesuchStatusEqual(antragJaxId.getId(), AntragStatusDTO.GEPRUEFT);

		Validate.notNull(antragJaxId.getId());
		final String antragId = converter.toEntityId(antragJaxId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(antragId);
		if (!gesuchOptional.isPresent()) {
			throw new EbeguEntityNotFoundException("closeWithoutAngebot", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + antragId);
		}

		Gesuch closedGesuch = gesuchService.closeWithoutAngebot(gesuchOptional.get());

		return Response.ok(converter.gesuchToJAX(closedGesuch)).build();
	}

	@ApiOperation(value = "Aendert den Status des Gesuchs auf VERFUEGEN. Sollte es nur Schulangebote geben, dann " +
		"wechselt der Status auf NUR_SCHULAMT", response = JaxGesuch.class)
	@Nullable
	@POST
	@Path("/verfuegenStarten/{antragId}/{hasFSDocument}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response verfuegenStarten(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@PathParam("hasFSDocument") boolean hasFSDocument,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		// Sicherstellen, dass der Status des Client-Objektes genau dem des Servers entspricht
		resourceHelper.assertGesuchStatusEqual(antragJaxId.getId(), AntragStatusDTO.GEPRUEFT);

		Validate.notNull(antragJaxId.getId());
		final String antragId = converter.toEntityId(antragJaxId);

		final Gesuch gesuch = gesuchService.findGesuch(antragId).orElseThrow(() -> new EbeguEntityNotFoundException("verfuegenStarten", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + antragId));
		gesuch.setHasFSDokument(hasFSDocument);
		Gesuch closedGesuch = gesuchService.verfuegenStarten(gesuch);

		return Response.ok(converter.gesuchToJAX(closedGesuch)).build();
	}

	@ApiOperation(value = "Ermittelt den Gesamtstatus aller Betreuungen des Gesuchs mit der uebergebenen Id.",
		response = GesuchBetreuungenStatus.class)
	@Nullable
	@GET
	@Path("/gesuchBetreuungenStatus/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response findGesuchBetreuungenStatus(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) {

		Validate.notNull(gesuchJAXPId.getId());
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(converter.toEntityId(gesuchJAXPId));
		if (!gesuchOptional.isPresent()) {
			throw new EbeguEntityNotFoundException("findGesuchBetreuungenStatus", ErrorCodeEnum
				.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + gesuchJAXPId.getId());
		}
		Gesuch gesuchToReturn = gesuchOptional.get();
		return Response.ok(gesuchToReturn.getGesuchBetreuungenStatus()).build();
	}

	@ApiOperation(value = "verfuegt das gegebene Gesuch. Funktioniert nur bei Gesuchen, bei denen alle Betreuungen verfügt sind, der Status"
		+ " vom Gesuch aber noch nicht als VERFUEGT gesetzt wurde.", response = JaxAntragSearchresultDTO.class)
	@Nonnull
	@POST
	@Path("/gesuchVerfuegen/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response gesuchVerfuegen(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) {

		Validate.notNull(gesuchJAXPId.getId());
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(converter.toEntityId(gesuchJAXPId));
		if (!gesuchOptional.isPresent()) {
			throw new EbeguEntityNotFoundException("gesuchVerfuegen", ErrorCodeEnum
				.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + gesuchJAXPId.getId());
		}
		gesuchService.gesuchVerfuegen(gesuchOptional.get());
		return Response.ok().build();
	}

	@ApiOperation(value = "Aendert den FinSitStatus im Gesuch", response = JaxGesuch.class)
	@Nonnull
	@POST
	@Path("/changeFinSitStatus/{antragId}/{finSitStatus}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeFinSitStatus(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Nonnull @NotNull @PathParam("finSitStatus") FinSitStatus finSitStatus,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(antragJaxId.getId());
		final String antragId = converter.toEntityId(antragJaxId);

		if (gesuchService.changeFinSitStatus(antragId, finSitStatus) == 1) {
			return Response.ok().build();
		}
		throw new EbeguEntityNotFoundException("changeFinSitStatus", ErrorCodeEnum
			.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + antragJaxId.getId());

	}

	@ApiOperation(value = "Ermittelt ob das uebergebene Gesuch das neuestes dieses Falls und Jahres ist.", response = Boolean.class)
	@Nullable
	@GET
	@Path("/newest/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response isNeuestesGesuch(@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) {
		Validate.notNull(gesuchJAXPId.getId());
		Gesuch gesuch = gesuchService.findGesuch(converter.toEntityId(gesuchJAXPId))
			.orElseThrow(() -> new EbeguEntityNotFoundException("isNeuestesGesuch", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchJAXPId.getId()));
		Boolean neustesGesuch = gesuchService.isNeustesGesuch(gesuch);
		return Response.ok(neustesGesuch).build();
	}

	@ApiOperation(value = "Gibt die ID des neuesten Gesuchs dieses Falls und Jahres zurueck. Wenn es noch keinen Fall, kein Gesuch oder keine Gesuchsperiode "
		+ "gibt, wird null zurueckgegeben", response = String.class)
	@Nonnull
	@GET
	@Path("/newestid/{gesuchsperiodeId}/{fallId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getIdOfNewestGesuch(@Nonnull @NotNull @PathParam("gesuchsperiodeId") JaxId gesuchsperiodeJaxId,
		@Nonnull @NotNull @PathParam("fallId") JaxId fallJaxId) {
		Validate.notNull(fallJaxId.getId());
		Validate.notNull(gesuchsperiodeJaxId.getId());

		Optional<Fall> fall = fallService.findFall(fallJaxId.getId());
		Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(gesuchsperiodeJaxId.getId());

		if (!fall.isPresent()) {
			throw new EbeguEntityNotFoundException("getIdOfNewestGesuch", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, fallJaxId.getId());
		}
		if (!gesuchsperiode.isPresent()) {
			throw new EbeguEntityNotFoundException("getIdOfNewestGesuch", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchsperiodeJaxId.getId());
		}
		Optional<String> idOfNeuestesGesuch = gesuchService.getIdOfNeuestesGesuch(gesuchsperiode.get(), fall.get());
		if (idOfNeuestesGesuch.isPresent()) {
			return Response.ok(idOfNeuestesGesuch.get()).build();
		}
		return Response.ok().build();
	}
}
