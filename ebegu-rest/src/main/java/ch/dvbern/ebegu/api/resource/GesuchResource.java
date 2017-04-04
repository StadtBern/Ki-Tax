package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAntragSearchresultDTO;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.dto.JaxAntragDTO;
import ch.dvbern.ebegu.dto.suchfilter.smarttable.AntragTableFilterDTO;
import ch.dvbern.ebegu.dto.suchfilter.smarttable.PaginationDTO;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.util.AntragStatusConverterUtil;
import ch.dvbern.ebegu.util.DateUtil;
import ch.dvbern.ebegu.util.MonitoringUtil;
import com.google.common.collect.ArrayListMultimap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.time.LocalDate;
import java.util.*;

/**
 * Resource fuer Gesuch
 */
@Path("gesuche")
@Stateless
@Api
public class GesuchResource {

	public static final String GESUCH_ID_INVALID = "GesuchId invalid: ";

	@Inject
	private GesuchService gesuchService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private BenutzerService benutzerService;

	private final Logger LOG = LoggerFactory.getLogger(GesuchResource.class.getSimpleName());

	@Inject
	private PrincipalBean principalBean;

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
		//only if status has changed
		final boolean saveInStatusHistory = gesuchToMerge.getStatus() != AntragStatusConverterUtil.convertStatusToEntity(gesuchJAXP.getStatus());
		Gesuch modifiedGesuch = this.gesuchService.updateGesuch(gesuchToMerge, saveInStatusHistory);

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
	 * Da beim Einscannen Gesuche eingelesen werden die noch im Status Freigabequittung sind brauchen
	 * wir hier eine separate Methode um das Lesen der noetigen Informationen dieser Gesuche zuzulassen
	 * Wenn kein Gesuch gefunden wird wird null zurueckgegeben.
	 * @param gesuchJAXPId gesuchID des Gesuchs im Status Freigabequittung oder hoeher
	 * @return DTO mit den relevanten Informationen zum Gesuch
	 */
	@Nullable
	@GET
	@Path("/freigabe/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxAntragDTO findGesuchForFreigabe(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) throws EbeguException {
		Validate.notNull(gesuchJAXPId.getId());
		String gesuchID = converter.toEntityId(gesuchJAXPId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuchForFreigabe(gesuchID);

		if (!gesuchOptional.isPresent()) {
			return null;
		}
		Gesuch gesuchToReturn = gesuchOptional.get();
		JaxAntragDTO jaxAntragDTO = converter.gesuchToAntragDTO(gesuchToReturn);
		jaxAntragDTO.setFamilienName(gesuchToReturn.extractFullnamesString()); //hier volle Namen beider GS
		return jaxAntragDTO;
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

		final Optional<Benutzer> optBenutzer = benutzerService.findBenutzer(this.principalBean.getPrincipal().getName());
		if (optBenutzer.isPresent()) {
			if (UserRole.SUPER_ADMIN.equals(optBenutzer.get().getRole())) {
				return completeGesuch;
			} else {
				Collection<Institution> instForCurrBenutzer = institutionService.getAllowedInstitutionenForCurrentBenutzer();
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

	@Nullable
	@PUT
	@Path("/bemerkung/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateBemerkung(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId,
		@Nonnull @NotNull String bemerkung,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(gesuchJAXPId.getId());
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(converter.toEntityId(gesuchJAXPId));

		if (gesuchOptional.isPresent()) {
			gesuchOptional.get().setBemerkungen(bemerkung);

			gesuchService.updateGesuch(gesuchOptional.get(), false);

			return Response.ok().build();
		}
		throw new EbeguEntityNotFoundException("updateBemerkung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + gesuchJAXPId.getId());
	}

	@Nullable
	@PUT
	@Path("/status/{gesuchId}/{statusDTO}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStatus(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId,
		@Nonnull @NotNull @PathParam("statusDTO") AntragStatusDTO statusDTO) throws EbeguException {

		Validate.notNull(gesuchJAXPId.getId());
		Validate.notNull(statusDTO);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(converter.toEntityId(gesuchJAXPId));

		if (gesuchOptional.isPresent()) {
			if (gesuchOptional.get().getStatus() != AntragStatusConverterUtil.convertStatusToEntity(statusDTO)) {
				//only if status has changed
				gesuchOptional.get().setStatus(AntragStatusConverterUtil.convertStatusToEntity(statusDTO));
				gesuchService.updateGesuch(gesuchOptional.get(), true);
			}
			return Response.ok().build();
		}
		LOG.error("Could not update Status because the Geusch with ID " + gesuchJAXPId.getId() + " could not be read");
		throw new EbeguEntityNotFoundException("updateStatus", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + gesuchJAXPId.getId());
	}

	@Nonnull
	@POST
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchAntraege(
		@Nonnull @NotNull AntragTableFilterDTO antragSearch,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		return MonitoringUtil.monitor(GesuchResource.class, "searchAntraege", () -> {
			Pair<Long, List<Gesuch>> searchResultPair = gesuchService.searchAntraege(antragSearch);
			List<Gesuch> foundAntraege = searchResultPair.getRight();

			Collection<Institution> allowedInst = institutionService.getAllowedInstitutionenForCurrentBenutzer();

			List<JaxAntragDTO> antragDTOList = new ArrayList<>(foundAntraege.size());
			foundAntraege.forEach(gesuch -> {
				JaxAntragDTO antragDTO = converter.gesuchToAntragDTO(gesuch, principalBean.discoverMostPrivilegedRole(), allowedInst);
				antragDTO.setFamilienName(gesuch.extractFamiliennamenString());
				antragDTOList.add(antragDTO);
			});
			JaxAntragSearchresultDTO resultDTO = new JaxAntragSearchresultDTO();
			resultDTO.setAntragDTOs(antragDTOList);
			PaginationDTO pagination = antragSearch.getPagination();
			pagination.setTotalItemCount(searchResultPair.getLeft());
			resultDTO.setPaginationDTO(pagination);
			return Response.ok(resultDTO).build();
		});
	}

	/**
	 * iteriert durch eine Liste von Antragen und gibt jeweils pro Fall nur den Antrag mit dem neusten Eingangsdatum zurueck
	 *
	 * @param foundAntraege Liste mit Antraegen, kann mehrere pro Fall enthalten
	 * @return Set mit Antraegen, jeweils nur der neuste zu einem bestimmten Fall
	 */
	@Nonnull
	@SuppressWarnings(value = {"unused"})
	@SuppressFBWarnings(value = "UPM_UNCALLED_PRIVATE_METHOD")
	private Set<Gesuch> reduceToNewestAntrag(List<Gesuch> foundAntraege) {
		ArrayListMultimap<Fall, Gesuch> fallToAntragMultimap = ArrayListMultimap.create();
		for (Gesuch gesuch : foundAntraege) {
			fallToAntragMultimap.put(gesuch.getFall(), gesuch);
		}
		Set<Gesuch> gesuchSet = new LinkedHashSet<>();
		for (Gesuch gesuch : foundAntraege) {
			List<Gesuch> antraege = fallToAntragMultimap.get(gesuch.getFall());
			Collections.sort(antraege, (Comparator<Gesuch>) (o1, o2) -> o1.getEingangsdatum().compareTo(o2.getEingangsdatum()));
			gesuchSet.add(antraege.get(0)); //nur neusten zurueckgeben
		}
		return gesuchSet;
	}


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

	@ApiOperation(value = "Creates a new Antrag of type Mutation in the database")
	@Nullable
	@POST
	@Path("/mutieren/{antragId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response antragMutieren(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Nullable @QueryParam("date") String stringDate,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

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

	@ApiOperation(value = "Gibt den Antrag frei und bereitet ihn vor für die Bearbeitung durch das Jugendamt")
	@Nullable
	@POST
	@Path("/freigeben/{antragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response antragFreigeben(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Nullable String username,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(antragJaxId.getId());

		final String antragId = converter.toEntityId(antragJaxId);

		Gesuch gesuch = gesuchService.antragFreigeben(antragId, username);
		return Response.ok(converter.gesuchToJAX(gesuch)).build();
	}

	@ApiOperation(value = "Setzt das gegebene Gesuch als Beschwerde hängig und bei allen Gescuhen der Periode den Flag gesperrtWegenBeschwerde auf true")
	@Nullable
	@POST
	@Path("/setBeschwerde/{antragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setBeschwerdeHaengig(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(antragJaxId.getId());
		final String antragId = converter.toEntityId(antragJaxId);
		Optional<Gesuch> gesuch = gesuchService.findGesuch(antragId);

		if (gesuch.isPresent()) {
			Gesuch persistedGesuch = gesuchService.setBeschwerdeHaengigForPeriode(gesuch.get());
			return Response.ok(converter.gesuchToJAX(persistedGesuch)).build();
		}
		throw new EbeguEntityNotFoundException("setBeschwerdeHaengig", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + antragJaxId.getId());
	}

	@ApiOperation(value = "Setzt das gegebene Gesuch als PRUEFUNG_STV")
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

		Validate.notNull(antragJaxId.getId());
		final String antragId = converter.toEntityId(antragJaxId);
		Optional<Gesuch> gesuch = gesuchService.findGesuch(antragId);

		if (!gesuch.isPresent()) {
			throw new EbeguEntityNotFoundException("sendGesuchToSTV", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + antragJaxId.getId());
		}
		if (!AntragStatus.VERFUEGT.equals(gesuch.get().getStatus())) {
			// Wir vergewissern uns dass das Gesuch im Status VERFUEGT ist, da sonst kann es nicht zum STV geschickt werden
			throw new EbeguRuntimeException("sendGesuchToSTV", ErrorCodeEnum.ERROR_ONLY_VERFUEGT_ALLOWED, "Status ist: " + gesuch.get().getStatus());
		}

		gesuch.get().setStatus(AntragStatus.PRUEFUNG_STV);
		if (StringUtils.isNotEmpty(bemerkungen)) {
			gesuch.get().setBemerkungenSTV(bemerkungen);
		}
		Gesuch persistedGesuch = gesuchService.updateGesuch(gesuch.get(), true);
		return Response.ok(converter.gesuchToJAX(persistedGesuch)).build();

	}

	@ApiOperation(value = "Setzt das gegebene Gesuch als VERFUEGT und bei allen Gescuhen der Periode den Flag gesperrtWegenBeschwerde auf false")
	@Nullable
	@POST
	@Path("/removeBeschwerde/{antragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeBeschwerdeHaengig(
		@Nonnull @NotNull @PathParam("antragId") JaxId antragJaxId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(antragJaxId.getId());
		final String antragId = converter.toEntityId(antragJaxId);
		Optional<Gesuch> gesuch = gesuchService.findGesuch(antragId);

		if (gesuch.isPresent()) {
			Gesuch persistedGesuch = gesuchService.removeBeschwerdeHaengigForPeriode(gesuch.get());
			return Response.ok(converter.gesuchToJAX(persistedGesuch)).build();
		}
		throw new EbeguEntityNotFoundException("removeBeschwerdeHaengig", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, GESUCH_ID_INVALID + antragJaxId.getId());
	}

	@GET
	@Path("/neuestesgesuch/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	public boolean isNeustesGesuch(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) throws EbeguException {
		Validate.notNull(gesuchJAXPId.getId());
		String gesuchID = converter.toEntityId(gesuchJAXPId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchID);
		return gesuchOptional.map(gesuch -> gesuchService.isNeustesGesuch(gesuch)).orElse(false);
	}
}
