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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
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

import static ch.dvbern.ebegu.enums.UserRole.ADMIN;
import static ch.dvbern.ebegu.enums.UserRole.JURIST;
import static ch.dvbern.ebegu.enums.UserRole.REVISOR;
import static ch.dvbern.ebegu.enums.UserRole.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRole.SUPER_ADMIN;

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
}
