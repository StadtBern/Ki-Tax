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
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAuthLoginElement;
import ch.dvbern.ebegu.services.BenutzerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * REST Resource fuer Benutzer  (Auf client userRS.rest.ts also eigentlich die UserResources)
 */
@Path("benutzer")
@Stateless
@Api(description = "Resource für die Verwaltung der Benutzer (User)")
public class BenutzerResource {

	@Inject
	private BenutzerService benutzerService;

	@Inject
	private JaxBConverter converter;

	@ApiOperation(value = "Gibt alle Benutzer zurück", responseContainer = "List", response = JaxAuthLoginElement.class)
	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxAuthLoginElement> getAllUsers() {
		return benutzerService.getAllBenutzer().stream()
			.map(benutzer -> converter.benutzerToAuthLoginElement(benutzer))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Gibt das Benutzer-Objekt des eingeloggten Benutzers zurück, falls es sich dabei um einen " +
		"Jugendamt-Benutzer oder Administrator handelt", responseContainer = "List", response = JaxAuthLoginElement.class)
	@Nonnull
	@GET
	@Path("/JAorAdmin")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxAuthLoginElement> getBenutzerJAorAdmin() {
		return benutzerService.getBenutzerJAorAdmin().stream()
			.map(benutzer -> converter.benutzerToAuthLoginElement(benutzer))
			.collect(Collectors.toList());
	}

	@ApiOperation(value = "Gibt das Benutzer-Objekt des eingeloggten Benutzers zurück, falls es sich dabei um einen " +
		"Gesuchsteller handelt", responseContainer = "List", response = JaxAuthLoginElement.class)
	@Nonnull
	@GET
	@Path("/gesuchsteller")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxAuthLoginElement> getGesuchsteller() {
		return benutzerService.getGesuchsteller().stream()
			.map(benutzer -> converter.benutzerToAuthLoginElement(benutzer))
			.collect(Collectors.toList());
	}
}
