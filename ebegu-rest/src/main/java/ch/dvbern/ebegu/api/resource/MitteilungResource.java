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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import ch.dvbern.ebegu.api.dtos.JaxBetreuungsmitteilung;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxMitteilung;
import ch.dvbern.ebegu.api.dtos.JaxMitteilungSearchresultDTO;
import ch.dvbern.ebegu.api.dtos.JaxMitteilungen;
import ch.dvbern.ebegu.dto.suchfilter.smarttable.MitteilungTableFilterDTO;
import ch.dvbern.ebegu.dto.suchfilter.smarttable.PaginationDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungsmitteilung;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.BetreuungService;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.MitteilungService;
import ch.dvbern.ebegu.util.MonitoringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Resource fuer Mitteilung
 */
@Path("mitteilungen")
@Stateless
@Api(description = "Resource zum Verwalten von Mitteilungen (In-System Nachrichten)")
public class MitteilungResource {

	public static final String FALL_ID_INVALID = "FallID invalid: ";

	@Inject
	private MitteilungService mitteilungService;

	@Inject
	private FallService fallService;

	@Inject
	private BetreuungService betreuungService;

	@Inject
	private JaxBConverter converter;

	@ApiOperation(value = "Speichert eine Mitteilung", response = JaxMitteilung.class)
	@Nullable
	@PUT
	@Path("/send")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung sendMitteilung(
		@Nonnull @NotNull @Valid JaxMitteilung mitteilungJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Mitteilung mitteilung = readAndConvertMitteilung(mitteilungJAXP);
		Mitteilung persistedMitteilung = this.mitteilungService.sendMitteilung(mitteilung);
		return converter.mitteilungToJAX(persistedMitteilung, new JaxMitteilung());
	}

	@ApiOperation(value = "Speichert eine BetreuungsMitteilung", response = JaxBetreuungsmitteilung.class)
	@Nullable
	@PUT
	@Path("/sendbetreuungsmitteilung")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxBetreuungsmitteilung sendBetreuungsmitteilung(
		@Nonnull @NotNull @Valid JaxBetreuungsmitteilung mitteilungJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Betreuungsmitteilung betreuungsmitteilung = converter.betreuungsmitteilungToEntity(mitteilungJAXP, new Betreuungsmitteilung());
		Betreuungsmitteilung persistedMitteilung = this.mitteilungService.sendBetreuungsmitteilung(betreuungsmitteilung);
		return converter.betreuungsmitteilungToJAX(persistedMitteilung);
	}

	@ApiOperation(value = "Uebernimmt eine Betreuungsmitteilung in eine Mutation. Falls aktuell keine Mutation offen " +
		"ist, wird eine neue erstellt. Falls eine Mutation im Status VERFUEGEN vorhanden ist, oder die Mutation im " +
		"Status BESCHWERDE ist, wird ein Fehler zurueckgegeben", response = JaxId.class)
	@Nullable
	@PUT
	@Path("/applybetreuungsmitteilung/{betreuungsmitteilungId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxId applyBetreuungsmitteilung(
		@Nonnull @NotNull @PathParam("betreuungsmitteilungId") JaxId betreuungsmitteilungId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		final Optional<Betreuungsmitteilung> mitteilung = mitteilungService.findBetreuungsmitteilung(betreuungsmitteilungId.getId());
		if (!mitteilung.isPresent()) {
			throw new EbeguEntityNotFoundException("applyBetreuungsmitteilung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND,
				"BetreuungsmitteilungID invalid: " + betreuungsmitteilungId.getId());
		}
		final Gesuch mutierteGesuch = this.mitteilungService.applyBetreuungsmitteilung(mitteilung.get());
		return converter.toJaxId(mutierteGesuch);
	}

	@ApiOperation(value = "Speichert eine Mitteilung als Entwurf", response = JaxMitteilung.class)
	@Nullable
	@PUT
	@Path("/entwurf")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung saveEntwurf(
		@Nonnull @NotNull @Valid JaxMitteilung mitteilungJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Mitteilung mitteilung = readAndConvertMitteilung(mitteilungJAXP);
		Mitteilung persistedMitteilung = this.mitteilungService.saveEntwurf(mitteilung);
		return converter.mitteilungToJAX(persistedMitteilung, new JaxMitteilung());
	}

	@ApiOperation(value = "Markiert eine Mitteilung als gelesen", response = JaxMitteilung.class)
	@Nullable
	@PUT
	@Path("/setgelesen/{mitteilungId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung setMitteilungGelesen(
		@Nonnull @NotNull @PathParam("mitteilungId") JaxId mitteilungId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		final Mitteilung mitteilung = mitteilungService.setMitteilungGelesen(mitteilungId.getId());

		return converter.mitteilungToJAX(mitteilung, new JaxMitteilung());
	}

	@ApiOperation(value = "Markiert eine Mitteilung als erledigt", response = JaxMitteilung.class)
	@Nullable
	@PUT
	@Path("/seterledigt/{mitteilungId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung setMitteilungErledigt(
		@Nonnull @NotNull @PathParam("mitteilungId") JaxId mitteilungId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		final Mitteilung mitteilung = mitteilungService.setMitteilungErledigt(mitteilungId.getId());

		return converter.mitteilungToJAX(mitteilung, new JaxMitteilung());
	}

	@ApiOperation(value = "Gibt die Mitteilung mit der uebergebenen Id zurueck", response = JaxMitteilung.class)
	@Nullable
	@GET
	@Path("/seterledigt/{mitteilungId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung findMitteilung(
		@Nonnull @NotNull @PathParam("mitteilungId") JaxId mitteilungId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(mitteilungId.getId());
		String mitteilungID = converter.toEntityId(mitteilungId);
		Optional<Mitteilung> optional = mitteilungService.findMitteilung(mitteilungID);

		return optional.map(mitteilung -> converter.mitteilungToJAX(mitteilung, new JaxMitteilung())).orElse(null);
	}

	@ApiOperation(value = "Gibt die neueste Betreuungsmitteilung fuer die uebergebene Betreuung zurueck",
		response = JaxMitteilung.class)
	@Nullable
	@GET
	@Path("/newestBetreuunsmitteilung/{betreuungId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung findNewestBetreuunsmitteilung(
		@Nonnull @NotNull @PathParam("betreuungId") JaxId jaxBetreuungId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(jaxBetreuungId.getId());
		String betreuungId = converter.toEntityId(jaxBetreuungId);
		Optional<Betreuung> optional = betreuungService.findBetreuung(betreuungId);

		if (!optional.isPresent()) {
			throw new EbeguEntityNotFoundException("findNewestBetreuunsmitteilung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, betreuungId);
		}

		Optional<Betreuungsmitteilung> optBetMitteilung = mitteilungService.findNewestBetreuungsmitteilung(betreuungId);
		return optBetMitteilung.map(mitteilung -> converter.betreuungsmitteilungToJAX(mitteilung)).orElse(null);
	}

	@ApiOperation(value = "Gibt einen Wrapper mit der Liste aller Mitteilungen zurueck, welche fuer den eingeloggten " +
		"Benutzer fuer den uebergebenen Fall vorhanden sind", response = JaxMitteilungen.class)
	@Nullable
	@GET
	@Path("/forrole/fall/{fallId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilungen getMitteilungenForCurrentRolleForFall(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(fallId.getId());
		String convertedFallID = converter.toEntityId(fallId);
		Optional<Fall> fall = fallService.findFall(convertedFallID);
		if (fall.isPresent()) {
			final Collection<JaxMitteilung> convertedMitteilungen = new ArrayList<>();
			final Collection<Mitteilung> mitteilungen = mitteilungService.getMitteilungenForCurrentRolle(fall.get());
			mitteilungen.forEach(mitteilung -> {
				if (mitteilung instanceof Betreuungsmitteilung) {
					convertedMitteilungen.add(converter.betreuungsmitteilungToJAX((Betreuungsmitteilung) mitteilung));
				} else {
					convertedMitteilungen.add(converter.mitteilungToJAX(mitteilung, new JaxMitteilung()));
				}
			});
			return new JaxMitteilungen(convertedMitteilungen); // We wrap the list to avoid loosing subtypes attributes
		}
		throw new EbeguEntityNotFoundException("getMitteilungenForCurrentRolle", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, FALL_ID_INVALID + fallId.getId());
	}

	@ApiOperation(value = "Gibt einen Wrapper mit der Liste aller Mitteilungen zurueck, welche fuer den eingeloggten " +
		"Benutzer fuer die uebergebene Betreuung vorhanden sind", response = JaxMitteilungen.class)
	@Nullable
	@GET
	@Path("/forrole/betreuung/{betreuungId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilungen getMitteilungenForCurrentRolleForBetreuung(
		@Nonnull @NotNull @PathParam("betreuungId") JaxId betreuungId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(betreuungId.getId());
		String id = converter.toEntityId(betreuungId);
		Optional<Betreuung> betreuung = betreuungService.findBetreuung(id);
		if (betreuung.isPresent()) {
			final Collection<Mitteilung> mitteilungen = mitteilungService.getMitteilungenForCurrentRolle(betreuung.get());
			return new JaxMitteilungen(mitteilungen.stream().map(mitteilung ->
				converter.mitteilungToJAX(mitteilung, new JaxMitteilung())).collect(Collectors.toList()));
		}
		throw new EbeguEntityNotFoundException("getMitteilungenForCurrentRolleForBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "BetreuungID invalid: " + betreuungId.getId());
	}

	@ApiOperation(value = "Ermittelt die Anzahl neuer Mitteilungen im Posteingang des eingeloggten Benutzers",
		response = Integer.class)
	@Nullable
	@GET
	@Path("/amountnewforuser/notokenrefresh")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Integer getAmountNewMitteilungenForCurrentBenutzer(
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		return mitteilungService.getAmountNewMitteilungenForCurrentBenutzer().intValue();
	}

	@ApiOperation(value = "Gibt fuer den uebergebenen Fall den vom eingeloggten Benutzer erstellten Entwurf zurueck, " +
		"falls einer vorhanden ist, sonst null", response = JaxMitteilung.class)
	@Nullable
	@GET
	@Path("/entwurf/fall/{fallId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung getEntwurfForCurrentRolleForFall(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(fallId.getId());
		String convertedFallID = converter.toEntityId(fallId);
		Optional<Fall> fall = fallService.findFall(convertedFallID);
		if (fall.isPresent()) {
			final Mitteilung mitteilung = mitteilungService.getEntwurfForCurrentRolle(fall.get());
			if (mitteilung == null) {
				return null;
			}
			return converter.mitteilungToJAX(mitteilung, new JaxMitteilung());
		}
		throw new EbeguEntityNotFoundException("getMitteilungenForCurrentRolle", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, FALL_ID_INVALID + fallId.getId());
	}

	@ApiOperation(value = "Gibt fuer die uebergebene Betreuung den vom eingeloggten Benutzer erstellten Entwurf zurueck, " +
		"falls einer vorhanden ist, sonst null", response = JaxMitteilung.class)
	@Nullable
	@GET
	@Path("/entwurf/betreuung/{betreuungId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung getEntwurfForCurrentRolleForBetreuung(
		@Nonnull @NotNull @PathParam("betreuungId") JaxId betreuungId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(betreuungId.getId());
		String id = converter.toEntityId(betreuungId);
		Optional<Betreuung> betreuung = betreuungService.findBetreuung(id);
		if (betreuung.isPresent()) {
			final Mitteilung mitteilung = mitteilungService.getEntwurfForCurrentRolle(betreuung.get());
			if (mitteilung == null) {
				return null;
			}
			return converter.mitteilungToJAX(mitteilung, new JaxMitteilung());
		}
		throw new EbeguEntityNotFoundException("getEntwurfForCurrentRolleForBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "BetreuungId invalid: " + betreuungId.getId());
	}

	@ApiOperation(value = "Loescht die Mitteilung mit der uebergebenen Id aus der Datenbank", response = Void.class)
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@Nullable
	@DELETE
	@Path("/{mitteilungId}")
	@Consumes(MediaType.WILDCARD)
	public Response removeMitteilung(
		@Nonnull @NotNull @PathParam("mitteilungId") JaxId mitteilungJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(mitteilungJAXPId.getId());
		Optional<Mitteilung> mitteilung = mitteilungService.findMitteilung(mitteilungJAXPId.getId());
		if (mitteilung.isPresent()) {
			mitteilungService.removeMitteilung(mitteilung.get());
			return Response.ok().build();
		}
		throw new EbeguEntityNotFoundException("removeMitteilung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "MitteilungID invalid: " + mitteilungJAXPId.getId());
	}

	@ApiOperation(value = "Setzt alle Mitteilungen des Falls mit der uebergebenen Id auf gelesen",
		response = JaxMitteilungen.class)
	@Nullable
	@PUT
	@Path("/setallgelesen/{fallId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilungen setAllNewMitteilungenOfFallGelesen(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(fallId.getId());
		String convertedFallID = converter.toEntityId(fallId);
		Optional<Fall> fall = fallService.findFall(convertedFallID);
		if (fall.isPresent()) {
			final Collection<Mitteilung> mitteilungen = mitteilungService.setAllNewMitteilungenOfFallGelesen(fall.get());
			Collection<JaxMitteilung> convertedMitteilungen = new ArrayList<>();
			final Iterator<Mitteilung> iterator = mitteilungen.iterator();
			//noinspection WhileLoopReplaceableByForEach
			while (iterator.hasNext()) {
				convertedMitteilungen.add(converter.mitteilungToJAX(iterator.next(), new JaxMitteilung()));
			}
			return new JaxMitteilungen(convertedMitteilungen);
		}
		throw new EbeguEntityNotFoundException("setAllNewMitteilungenOfFallGelesen", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, FALL_ID_INVALID + fallId.getId());
	}

	private Mitteilung readAndConvertMitteilung(@Nonnull JaxMitteilung mitteilungJAXP) {
		Mitteilung mitteilung = new Mitteilung();
		if (mitteilungJAXP.getId() != null) {
			final Optional<Mitteilung> optMitteilung = mitteilungService.findMitteilung(mitteilungJAXP.getId());
			mitteilung = optMitteilung.orElse(new Mitteilung());
		}

		return converter.mitteilungToEntity(mitteilungJAXP, mitteilung);
	}

	@ApiOperation(value = "Ermittelt die Anzahl neuer Mitteilungen aller Benutzer in der Rolle des eingeloggten Benutzers", response = Integer.class)
	@Nullable
	@GET
	@Path("/amountnew/{fallId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Integer getAmountNewMitteilungenForCurrentRolle(
		@Nonnull @NotNull @PathParam("fallId") JaxId jaxFallId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(jaxFallId.getId());
		String fallId = converter.toEntityId(jaxFallId);
		Optional<Fall> fall = fallService.findFall(fallId);
		if (fall.isPresent()) {
			return mitteilungService.getNewMitteilungenForCurrentRolleAndFall(fall.get()).size();
		}
		throw new EbeguEntityNotFoundException("getMitteilungenForCurrentRolle", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, FALL_ID_INVALID + jaxFallId.getId());
	}

	@ApiOperation(value = "Uebergibt die Mitteilung vom Schulamt ans Jugendamt", response = JaxMitteilung.class)
	@Nonnull
	@GET
	@Path("/delegation/jugendamt/{mitteilungId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung mitteilungUebergebenAnJugendamt(
		@Nonnull @NotNull @PathParam("mitteilungId") JaxId mitteilungJaxId, @Context UriInfo uriInfo, @Context HttpServletResponse response) {

		Validate.notNull(mitteilungJaxId.getId());
		String mitteilungId = converter.toEntityId(mitteilungJaxId);
		Mitteilung mitteilung = mitteilungService.mitteilungUebergebenAnJugendamt(mitteilungId);
		return converter.mitteilungToJAX(mitteilung, new JaxMitteilung());
	}

	@ApiOperation(value = "Uebergibt die Mitteilung vom Jugendamt ans Schulamt", response = JaxMitteilung.class)
	@Nonnull
	@GET
	@Path("/delegation/schulamt/{mitteilungId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMitteilung mitteilungUebergebenAnSchulamt(
		@Nonnull @NotNull @PathParam("mitteilungId") JaxId mitteilungJaxId, @Context UriInfo uriInfo, @Context HttpServletResponse response) {

		Validate.notNull(mitteilungJaxId.getId());
		String mitteilungId = converter.toEntityId(mitteilungJaxId);
		Mitteilung mitteilung = mitteilungService.mitteilungUebergebenAnSchulamt(mitteilungId);
		return converter.mitteilungToJAX(mitteilung, new JaxMitteilung());
	}

	@ApiOperation(value = "Sucht Mitteilungen mit den uebergebenen Suchkriterien/Filtern", response = JaxMitteilungSearchresultDTO.class)
	@Nonnull
	@POST
	@Path("/search/{includeClosed}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchMitteilungen(
		@Nonnull @PathParam("includeClosed") String includeClosed,
		@Nonnull @NotNull MitteilungTableFilterDTO tableFilterDTO,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		return MonitoringUtil.monitor(GesuchResource.class, "searchMitteilungen", () -> {
			Pair<Long, List<Mitteilung>> searchResultPair = mitteilungService.searchMitteilungen(tableFilterDTO, Boolean.valueOf(includeClosed));
			List<Mitteilung> foundMitteilungen = searchResultPair.getRight();

			List<JaxMitteilung> convertedMitteilungen = foundMitteilungen.stream().map(mitteilung ->
				converter.mitteilungToJAX(mitteilung, new JaxMitteilung())).collect(Collectors.toList());

			JaxMitteilungSearchresultDTO resultDTO = new JaxMitteilungSearchresultDTO();
			resultDTO.setMitteilungDTOs(convertedMitteilungen);
			PaginationDTO pagination = tableFilterDTO.getPagination();
			pagination.setTotalItemCount(searchResultPair.getLeft());
			resultDTO.setPaginationDTO(pagination);

			return Response.ok(resultDTO).build();
		});
	}
}
