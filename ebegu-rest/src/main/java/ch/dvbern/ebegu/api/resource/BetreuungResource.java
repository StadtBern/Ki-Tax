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

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxBetreuung;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.resource.util.ResourceHelper;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * REST Resource fuer Betreuungen. Betreuung = ein Kind in einem Betreuungsangebot bei einer Institution.
 */
@Path("betreuungen")
@Stateless
@Api(description = "Resource zum Verwalten von Betreuungen (Ein Betreuungsangebot für ein Kind)")
public class BetreuungResource {

	@Inject
	private BetreuungService betreuungService;
	@Inject
	private KindService kindService;
	@Inject
	private FallService fallService;
	@Inject
	private JaxBConverter converter;
	@Inject
	private ResourceHelper resourceHelper;
	@Inject
	private GesuchService gesuchService;


	//TODO (hefr) Dieser Service wird immer nur fuer Betreuungen verwendet, nie fuer Abwesenheiten
	@ApiOperation(value = "Speichert eine Betreuung in der Datenbank", response = JaxBetreuung.class)
	@Nonnull
	@PUT
	@Path("/betreuung/{kindId}/{abwesenheit}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxBetreuung saveBetreuung(
		@Nonnull @NotNull @PathParam("kindId") JaxId kindId,
		@Nonnull @NotNull @Valid JaxBetreuung betreuungJAXP,
		@Nonnull @NotNull @PathParam ("abwesenheit") Boolean abwesenheit,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<KindContainer> kind = kindService.findKind(kindId.getId());
		if (kind.isPresent()) {
			resourceHelper.assertGesuchStatusForBenutzerRole(kind.get().getGesuch());
			Betreuung convertedBetreuung = converter.betreuungToStoreableEntity(betreuungJAXP);
			convertedBetreuung.setKind(kind.get());
			Betreuung persistedBetreuung = this.betreuungService.saveBetreuung(convertedBetreuung, abwesenheit);

			return converter.betreuungToJAX(persistedBetreuung);
		}
		throw new EbeguEntityNotFoundException("saveBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "KindContainerId invalid: " + kindId.getId());
	}

	//TODO (hefr) dieser service wird immer nur fuer Abwesenheiten verwendet
	@ApiOperation(value = "Speichert eine Abwesenheit in der Datenbank.", responseContainer = "List", response = JaxBetreuung.class)
	@Nonnull
	@PUT
	@Path("/all/{abwesenheit}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxBetreuung> saveBetreuungen(
		@Nonnull @NotNull @Valid List<JaxBetreuung> betreuungenJAXP,
		@Nonnull @NotNull @PathParam ("abwesenheit") Boolean abwesenheit,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		if (!betreuungenJAXP.isEmpty() && betreuungenJAXP.get(0).getGesuchId() != null) {
			final Optional<Gesuch> gesuch = gesuchService.findGesuch(betreuungenJAXP.get(0).getGesuchId());
			gesuch.ifPresent(gesuch1 -> resourceHelper.assertGesuchStatusForBenutzerRole(gesuch1));
		}

		List<JaxBetreuung> resultBetreuungen = new ArrayList<>();
		betreuungenJAXP.forEach(betreuungJAXP -> {
			Betreuung convertedBetreuung = converter.betreuungToStoreableEntity(betreuungJAXP);
			Betreuung persistedBetreuung = this.betreuungService.saveBetreuung(convertedBetreuung, abwesenheit);

			resultBetreuungen.add(converter.betreuungToJAX(persistedBetreuung));
		});
		return resultBetreuungen;
	}

	@ApiOperation(value = "Betreuungsplatzanfrage wird durch die Institution abgelehnt", response = JaxBetreuung.class)
	@Nonnull
	@PUT
	@Path("/abweisen/{kindId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxBetreuung betreuungPlatzAbweisen(
		@Nonnull @NotNull @PathParam("kindId") JaxId kindId,
		@Nonnull @NotNull @Valid JaxBetreuung betreuungJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(betreuungJAXP.getId());
		// Sicherstellen, dass der Status des Client-Objektes genau dem des Servers entspricht
		resourceHelper.assertBetreuungStatusEqual(betreuungJAXP.getId(), Betreuungsstatus.WARTEN);

		Optional<KindContainer> kind = kindService.findKind(kindId.getId());
		if (kind.isPresent()) {
			// Sicherstellen, dass das dazugehoerige Gesuch ueberhaupt noch editiert werden darf fuer meine Rolle
			resourceHelper.assertGesuchStatusForBenutzerRole(kind.get().getGesuch());

			Betreuung convertedBetreuung = converter.betreuungToStoreableEntity(betreuungJAXP);
			convertedBetreuung.setKind(kind.get());
			Betreuung persistedBetreuung = this.betreuungService.betreuungPlatzAbweisen(convertedBetreuung);

			return converter.betreuungToJAX(persistedBetreuung);
		}
		throw new EbeguEntityNotFoundException("saveBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "KindContainerId invalid: " + kindId.getId());
	}

	@ApiOperation(value = "Betreuungsplatzanfrage wird durch die Institution bestätigt", response = JaxBetreuung.class)
	@Nonnull
	@PUT
	@Path("/bestaetigen/{kindId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxBetreuung betreuungPlatzBestaetigen(
		@Nonnull @NotNull @PathParam("kindId") JaxId kindId,
		@Nonnull @NotNull @Valid JaxBetreuung betreuungJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(betreuungJAXP.getId());

		// Sicherstellen, dass der Status des Client-Objektes genau dem des Servers entspricht
		resourceHelper.assertBetreuungStatusEqual(betreuungJAXP.getId(), Betreuungsstatus.WARTEN);

		Optional<KindContainer> kind = kindService.findKind(kindId.getId());
		if (kind.isPresent()) {
			// Sicherstellen, dass das dazugehoerige Gesuch ueberhaupt noch editiert werden darf fuer meine Rolle
			resourceHelper.assertGesuchStatusForBenutzerRole(kind.get().getGesuch());

			Betreuung convertedBetreuung = converter.betreuungToStoreableEntity(betreuungJAXP);
			convertedBetreuung.setKind(kind.get());
			Betreuung persistedBetreuung = this.betreuungService.betreuungPlatzBestaetigen(convertedBetreuung);

			return converter.betreuungToJAX(persistedBetreuung);
		}
		throw new EbeguEntityNotFoundException("saveBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "KindContainerId invalid: " + kindId.getId());
	}

	@ApiOperation(value = "Sucht die Betreuung mit der übergebenen Id in der Datenbank. Dabei wird geprüft, ob der " +
		"eingeloggte Benutzer für die gesuchte Betreuung berechtigt ist", response = JaxBetreuung.class)
	@Nullable
	@GET
	@Path("/{betreuungId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxBetreuung findBetreuung(
		@Nonnull @NotNull @PathParam("betreuungId") JaxId betreuungJAXPId) throws EbeguException {
		Validate.notNull(betreuungJAXPId.getId());
		String id = converter.toEntityId(betreuungJAXPId);
		Optional<Betreuung> fallOptional = betreuungService.findBetreuung(id);

		if (!fallOptional.isPresent()) {
			return null;
		}
		Betreuung betreuungToReturn = fallOptional.get();
		return converter.betreuungToJAX(betreuungToReturn);
	}

	@ApiOperation(value = "Löscht die Betreuung mit der übergebenen Id in der Datenbank. Dabei wird geprüft, ob der " +
		"eingeloggte Benutzer für die gesuchte Betreuung berechtigt ist", response = Void.class)
	@Nullable
	@DELETE
	@Path("/{betreuungId}")
	@Consumes(MediaType.WILDCARD)
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	public Response removeBetreuung(
		@Nonnull @NotNull @PathParam("betreuungId") JaxId betreuungJAXPId,
		@Context HttpServletResponse response) {

		Validate.notNull(betreuungJAXPId.getId());
		Optional<Betreuung> betreuung = betreuungService.findBetreuung(betreuungJAXPId.getId());

		if (betreuung.isPresent()) {
			// Sicherstellen, dass das dazugehoerige Gesuch ueberhaupt noch editiert werden darf fuer meine Rolle
			resourceHelper.assertGesuchStatusForBenutzerRole(betreuung.get().extractGesuch());
			betreuungService.removeBetreuung(converter.toEntityId(betreuungJAXPId));
			return Response.ok().build();
		}
		throw new EbeguEntityNotFoundException("removeBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "BetreuungID invalid: " + betreuungJAXPId.getId());
	}

	@ApiOperation(value = "Sucht alle verfügten Betreuungen aus allen Gesuchsperioden, welche zum übergebenen Fall " +
		"vorhanden sind. Es werden nur diejenigen Betreuungen zurückgegeben, für welche der eingeloggte Benutzer " +
		"berechtigt ist.", responseContainer = "Collection", response = JaxBetreuung.class)
	@Nullable
	@GET
	@Path("/alleBetreuungen/{fallId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAllBetreuungenWithVerfuegungFromFall(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<Fall> fallOptional = fallService.findFall(converter.toEntityId(fallId));

		if (!fallOptional.isPresent()) {
			return null;
		}
		Fall fall = fallOptional.get();

		Collection<Betreuung> betreuungCollection = betreuungService.findAllBetreuungenWithVerfuegungFromFall(fall);
		Collection<JaxBetreuung> jaxBetreuungList = converter.betreuungListToJax(betreuungCollection);

		return Response.ok(jaxBetreuungList).build();
	}
}
