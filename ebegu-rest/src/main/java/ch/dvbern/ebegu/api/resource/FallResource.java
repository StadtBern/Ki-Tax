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

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxFall;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.services.FallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

/**
 * Resource fuer Fall
 */
@Path("falle")
@Stateless
@Api(description = "Resource zum Verwalten von Fällen (Familien)")
public class FallResource {

	@Inject
	private FallService fallService;

	@Inject
	private BenutzerService benutzerService;

	@Inject
	private JaxBConverter converter;

	@ApiOperation(value = "Creates a new Fall in the database. The transfer object also has a relation to Gesuch " +
		"which is stored in the database as well.", response = JaxFall.class)
	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFall saveFall(
		@Nonnull @NotNull @Valid JaxFall fallJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Fall fall = new Fall();
		if (fallJAXP.getId() != null) {
			Optional<Fall> optional = fallService.findFall(fallJAXP.getId());
			fall = optional.orElse(new Fall());
		}
		Fall convertedFall = converter.fallToEntity(fallJAXP, fall);

		Fall persistedFall = this.fallService.saveFall(convertedFall);
		return converter.fallToJAX(persistedFall);
	}

	@ApiOperation(value = "Returns the Fall with the given Id.", response = JaxFall.class)
	@Nullable
	@GET
	@Path("/id/{fallId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFall findFall(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallJAXPId) {
		Validate.notNull(fallJAXPId.getId());
		String fallID = converter.toEntityId(fallJAXPId);
		Optional<Fall> fallOptional = fallService.findFall(fallID);

		if (!fallOptional.isPresent()) {
			return null;
		}
		Fall fallToReturn = fallOptional.get();
		return converter.fallToJAX(fallToReturn);
	}

	@ApiOperation(value = "Returns the Fall having the current Benutzer as owner", response = JaxFall.class)
	@Nullable
	@GET
	@Path("/currentbenutzer")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFall findFallByCurrentBenutzerAsBesitzer() {
		Optional<Fall> fallOptional = fallService.findFallByCurrentBenutzerAsBesitzer();
		if (!fallOptional.isPresent()) {
			return null;
		}
		Fall fallToReturn = fallOptional.get();
		return converter.fallToJAX(fallToReturn);
	}

	@ApiOperation(value = "Creates a new Fall in the database with the current user as owner.", response = JaxFall.class)
	@Nullable
	@PUT
	@Path("/createforcurrentbenutzer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFall createFallForCurrentGesuchstellerAsBesitzer(
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Optional<Fall> fallOptional = fallService.createFallForCurrentGesuchstellerAsBesitzer();
		if (!fallOptional.isPresent()) {
			return null;
		}
		Fall fallToReturn = fallOptional.get();
		return converter.fallToJAX(fallToReturn);
	}

	@ApiOperation(value = "Setzt den Verantwortlichen JA fuer diesen Fall.", response = JaxFall.class)
	@Nullable
	@PUT
	@Path("/verantwortlicherJA/{fallId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFall setVerantwortlicherJA(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallJaxId,
		@Nonnull @NotNull String username,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(fallJaxId.getId());
		Validate.notNull(username);

		Benutzer benutzer = benutzerService.findBenutzer(username).orElseThrow(() -> new EbeguEntityNotFoundException("setVerantwortlicherJA",
			ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, username));
		Fall fall = fallService.findFall(fallJaxId.getId()).orElseThrow(() -> new EbeguEntityNotFoundException("setVerantwortlicherJA",
			ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, fallJaxId.getId()));

		return converter.fallToJAX(this.fallService.setVerantwortlicherJA(fall.getId(), benutzer));
	}

	@ApiOperation(value = "Setzt den Verantwortlichen SCH fuer diesen Fall.", response = JaxFall.class)
	@Nullable
	@PUT
	@Path("/verantwortlicherSCH/{fallId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFall setVerantwortlicherSCH(
		@Nonnull @NotNull @PathParam("fallId") JaxId fallJaxId,
		@Nonnull @NotNull String username,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) {

		Validate.notNull(fallJaxId.getId());
		Validate.notNull(username);

		Benutzer benutzer = benutzerService.findBenutzer(username).orElseThrow(() -> new EbeguEntityNotFoundException("setVerantwortlicherSCH",
			ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, username));
		Fall fall = fallService.findFall(fallJaxId.getId()).orElseThrow(() -> new EbeguEntityNotFoundException("setVerantwortlicherSCH",
			ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, fallJaxId.getId()));

		return converter.fallToJAX(this.fallService.setVerantwortlicherSCH(fall.getId(), benutzer));
	}
}
