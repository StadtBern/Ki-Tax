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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
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
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxMahnung;
import ch.dvbern.ebegu.api.resource.util.ResourceHelper;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.enums.MahnungTyp;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.MahnungService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

/**
 * Resource fuer Mahnungen
 */
@Path("mahnung")
@Stateless
@Api(description = "Resource zum Verwalten eines Mahnlaufes")
public class MahnungResource {

	@Inject
	private GesuchService gesuchService;

	@Inject
	private MahnungService mahnungService;

	@Inject
	private JaxBConverter converter;

	@Inject
	private ResourceHelper resourceHelper;

	@ApiOperation(value = "Speichert eine Mahnung in der Datenbank", response = JaxMahnung.class)
	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMahnung save(
		@Nonnull @NotNull JaxMahnung mahnungJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(mahnungJAXP);
		Validate.notNull(mahnungJAXP.getGesuch());
		Validate.notNull(mahnungJAXP.getGesuch().getId());

		// Sicherstellen, dass der Status des Client-Objektes genau dem des Servers entspricht
		if (MahnungTyp.ERSTE_MAHNUNG == mahnungJAXP.getMahnungTyp()) {
			resourceHelper.assertGesuchStatusEqual(mahnungJAXP.getGesuch().getId(), AntragStatusDTO.IN_BEARBEITUNG_JA);
		} else {
			resourceHelper.assertGesuchStatusEqual(mahnungJAXP.getGesuch().getId(), AntragStatusDTO.ERSTE_MAHNUNG_ABGELAUFEN);
		}

		Mahnung mahnung = converter.mahnungToEntity(mahnungJAXP, new Mahnung());
		Mahnung persistedMahnung = mahnungService.createMahnung(mahnung);

		return converter.mahnungToJAX(persistedMahnung);
	}

	@ApiOperation(value = "Gibt alle Mahnungen zum Gesuch mit der uebergebenen Id zurueck",
		responseContainer = "List", response = JaxMahnung.class)
	@Nullable
	@GET
	@Path("/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxMahnung> findMahnungen(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) throws EbeguException {
		Validate.notNull(gesuchJAXPId.getId());
		String gesuchID = converter.toEntityId(gesuchJAXPId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchID);

		if (!gesuchOptional.isPresent()) {
			return null;
		}
		Gesuch gesuchToReturn = gesuchOptional.get();

		return mahnungService.findMahnungenForGesuch(gesuchToReturn).stream()
			.map(mahnung -> converter.mahnungToJAX(mahnung))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Beendet einen Mahnlauf und setzt alle vorhandenen Mahnungen auf erledigt. Der Gesuchsstatus " +
		"geht zurueck auf IN_BEARBEITUNG_JA.", response = JaxGesuch.class)
	@Nonnull
	@PUT
	@Path("/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response mahnlaufBeenden(@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) {
		Validate.notNull(gesuchJAXPId.getId());

		resourceHelper.assertGesuchStatusEqual(gesuchJAXPId.getId(),
			AntragStatusDTO.ERSTE_MAHNUNG, AntragStatusDTO.ERSTE_MAHNUNG_ABGELAUFEN,
			AntragStatusDTO.ZWEITE_MAHNUNG, AntragStatusDTO.ZWEITE_MAHNUNG_ABGELAUFEN);

		String gesuchID = converter.toEntityId(gesuchJAXPId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchID);

		if (!gesuchOptional.isPresent()) {
			return Response.serverError().build();
		}

		final Gesuch gesuchToReturn = mahnungService.mahnlaufBeenden(gesuchOptional.get());

		return Response.ok(converter.gesuchToJAX(gesuchToReturn)).build();
	}

	@ApiOperation(value = "Generiert die Bemerkungen fuer eine zu erstellende Mahnung. Die Bemerkungen werden aus den" +
		" fehlenden Dokumenten zusammengestellt.", response = String.class)
	@Nonnull
	@GET
	@Path("/bemerkungen/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	public String getInitialeBemerkungen(@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) {
		Validate.notNull(gesuchJAXPId.getId());
		String gesuchID = converter.toEntityId(gesuchJAXPId);
		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchID);

		if (!gesuchOptional.isPresent()) {
			return "";
		}
		Gesuch gesuch = gesuchOptional.get();
		return mahnungService.getInitialeBemerkungen(gesuch);
	}
}
