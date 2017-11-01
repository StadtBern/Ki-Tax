package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAdresse;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxZahlung;
import ch.dvbern.ebegu.api.dtos.JaxZahlungsauftrag;
import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Zahlung;
import ch.dvbern.ebegu.entities.Zahlungsauftrag;
import ch.dvbern.ebegu.enums.ZahlungauftragStatus;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.GeneratedDokumentService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.ZahlungService;
import ch.dvbern.ebegu.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.dvbern.ebegu.enums.UserRole.*;

/**
 * Resource fuer Zahlungen
 */
@Path("zahlungen")
@Stateless
@Api(description = "Resource zum Verwalten von Zahlungen")
public class ZahlungResource {


	@Inject
	private ZahlungService zahlungService;

	@Inject
	private JaxBConverter converter;

	@Inject
	private GeneratedDokumentService generatedDokumentService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private PrincipalBean principalBean;


	@ApiOperation(value = "Gibt alle Zahlungsauftraege zurueck.",
		responseContainer = "List", response = JaxZahlungsauftrag.class)
	@Nullable
	@GET
	@Path("/all")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxZahlungsauftrag> getAllZahlungsauftraege() {
		if (principalBean.isCallerInAnyOfRole(ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR)) {
			return zahlungService.getAllZahlungsauftraege().stream()
				.map(zahlungsauftrag -> converter.zahlungsauftragToJAX(zahlungsauftrag, false))
				.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@ApiOperation(value = "Gibt alle Zahlungsauftraege aller Institutionen zurueck, fuer welche der eingeloggte " +
		"Benutzer zustaendig ist.",
		responseContainer = "List", response = JaxZahlungsauftrag.class)
	@Nullable
	@GET
	@Path("/institution")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxZahlungsauftrag> getAllZahlungsauftraegeInstitution() {

		Collection<Institution> allowedInst = institutionService.getAllowedInstitutionenForCurrentBenutzer();

		return zahlungService.getAllZahlungsauftraege().stream()
			.filter(zahlungsauftrag -> !zahlungsauftrag.getStatus().equals(ZahlungauftragStatus.ENTWURF))
			.map(zahlungsauftrag -> converter.zahlungsauftragToJAX(zahlungsauftrag, principalBean.discoverMostPrivilegedRole(), allowedInst))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Gibt den Zahlungsauftrag mit der uebebergebenen Id zurueck.",
		response = JaxZahlungsauftrag.class)
	@Nullable
	@GET
	@Path("/zahlungsauftrag/{zahlungsauftragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxZahlungsauftrag findZahlungsauftrag(
		@Nonnull @NotNull @PathParam("zahlungsauftragId") JaxId zahlungsauftragJAXPId) throws EbeguException {

		if (principalBean.isCallerInAnyOfRole(ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR)) {
			Validate.notNull(zahlungsauftragJAXPId.getId());
			String zahlungsauftragId = converter.toEntityId(zahlungsauftragJAXPId);
			Optional<Zahlungsauftrag> optional = zahlungService.findZahlungsauftrag(zahlungsauftragId);

			if (!optional.isPresent()) {
				return null;
			}
			return converter.zahlungsauftragToJAX(optional.get(), true);
		}
		return new JaxZahlungsauftrag();
	}

	@ApiOperation(value = "Gibt den Zahlungsauftrag mit der uebebergebenen Id zurueck, jedoch nur mit den Eintraegen " +
		"derjenigen Institutionen, fuer welche der eingeloggte Benutzer zustaendig ist",
		response = JaxZahlungsauftrag.class)
	@Nullable
	@GET
	@Path("/zahlungsauftraginstitution/{zahlungsauftragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxZahlungsauftrag findZahlungsauftraginstitution(
		@Nonnull @NotNull @PathParam("zahlungsauftragId") JaxId zahlungsauftragJAXPId) throws EbeguException {

		Validate.notNull(zahlungsauftragJAXPId.getId());
		String zahlungsauftragId = converter.toEntityId(zahlungsauftragJAXPId);
		Optional<Zahlungsauftrag> optional = zahlungService.findZahlungsauftrag(zahlungsauftragId);

		if (!optional.isPresent()) {
			return null;
		}
		Collection<Institution> allowedInst = institutionService.getAllowedInstitutionenForCurrentBenutzer();

		return converter.zahlungsauftragToJAX(optional.get(), principalBean.discoverMostPrivilegedRole(), allowedInst);
	}

	@ApiOperation(value = "Setzt den Status des Zahlungsautrags auf ausgeloest. Danach kann er nicht mehr veraendert " +
		"werden", response = JaxZahlungsauftrag.class)
	@Nullable
	@PUT
	@Path("/ausloesen/{zahlungsauftragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxZahlungsauftrag zahlungsauftragAusloesen(
		@Nonnull @NotNull @PathParam("zahlungsauftragId") JaxId zahlungsauftragJAXPId) throws EbeguException, MimeTypeParseException {

		Validate.notNull(zahlungsauftragJAXPId.getId());
		String zahlungsauftragId = converter.toEntityId(zahlungsauftragJAXPId);

		final Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragAusloesen(zahlungsauftragId);

		//Force creation and saving of ZahlungsFile Pain001
		generatedDokumentService.getPain001DokumentAccessTokenGeneratedDokument(zahlungsauftrag, true);

		return converter.zahlungsauftragToJAX(zahlungsauftrag, false);
	}

	@ApiOperation(value = "Erstellt einen neue Zahlungsauftrag", response = JaxZahlungsauftrag.class)
	@Nullable
	@GET
	@Path("/create")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxZahlungsauftrag createZahlung(
		@QueryParam("faelligkeitsdatum") String stringFaelligkeitsdatum,
		@QueryParam("beschrieb") String beschrieb,
		@Nullable @QueryParam("datumGeneriert") String stringDatumGeneriert) throws EbeguRuntimeException {

		LocalDate faelligkeitsdatum = DateUtil.parseStringToDateOrReturnNow(stringFaelligkeitsdatum);
		LocalDateTime datumGeneriert;
		if (stringDatumGeneriert != null) {
			datumGeneriert = DateUtil.parseStringToDateOrReturnNow(stringDatumGeneriert).atStartOfDay();
		} else {
			datumGeneriert = LocalDateTime.now();
		}

		final Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(faelligkeitsdatum, beschrieb, datumGeneriert);

		return converter.zahlungsauftragToJAX(zahlungsauftrag, false);
	}

	@ApiOperation(value = "Aktualisiert einen Zahlungsauftrag", response = JaxZahlungsauftrag.class)
	@Nullable
	@GET
	@Path("/update")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxZahlungsauftrag updateZahlung(
		@QueryParam("beschrieb") String beschrieb,
		@QueryParam("faelligkeitsdatum") String stringFaelligkeitsdatum,
		@QueryParam("id") String id) throws EbeguRuntimeException {

		LocalDate faelligkeitsdatum = DateUtil.parseStringToDateOrReturnNow(stringFaelligkeitsdatum);
		final Zahlungsauftrag zahlungsauftragUpdated = zahlungService.zahlungsauftragAktualisieren(id, faelligkeitsdatum, beschrieb);
		return converter.zahlungsauftragToJAX(zahlungsauftragUpdated, false);
	}

	@ApiOperation(value = "Setzt eine Zahlung eines Zahlungsauftrags auf bestaetigt", response = JaxZahlung.class)
	@Nullable
	@PUT
	@Path("/bestaetigen/{zahlungId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxZahlung zahlungBestaetigen(
		@Nonnull @NotNull @PathParam("zahlungId") JaxId zahlungJAXPId) throws EbeguException, MimeTypeParseException {

		Validate.notNull(zahlungJAXPId.getId());
		String zahlungId = converter.toEntityId(zahlungJAXPId);

		final Zahlung zahlung = zahlungService.zahlungBestaetigen(zahlungId);
		return converter.zahlungToJAX(zahlung);
	}

	@ApiOperation(value = "Loescht einen Zahlungsauftrag", response = Void.class)
	@Nullable
	@DELETE
	@Path("/delete/{zahlungsauftragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response zahlungsauftragLoeschen(
		@Nonnull @NotNull @PathParam("zahlungsauftragId") JaxId zahlungsauftragJAXPId) throws EbeguException, MimeTypeParseException {

		Validate.notNull(zahlungsauftragJAXPId.getId());
		String zahlungsauftragId = converter.toEntityId(zahlungsauftragJAXPId);

		zahlungService.deleteZahlungsauftrag(zahlungsauftragId);
		return Response.ok().build();
	}

	@ApiOperation(value = "Zahlungsauftrag kontrollieren", response = Void.class)
	@GET
	@Path("/kontrollieren")
	public Response zahlungenKontrollieren() {
		zahlungService.zahlungenKontrollieren();
		return Response.ok().build();
	}
}
