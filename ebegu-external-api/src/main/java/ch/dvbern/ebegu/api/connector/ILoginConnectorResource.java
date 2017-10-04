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

package ch.dvbern.ebegu.api.connector;

import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ch.dvbern.ebegu.api.dtos.JaxExternalAuthAccessElement;
import ch.dvbern.ebegu.api.dtos.JaxExternalAuthorisierterBenutzer;
import ch.dvbern.ebegu.api.dtos.JaxExternalBenutzer;

@Path("/connector")
public interface ILoginConnectorResource {

	/**
	 * this service should be callable without authentication and can serve as a smoke test to see
	 * if the deploymentw as ok
	 */
	@GET
	@Path("/heartbeat")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	String getHeartBeat();

	/**
	 * Service to create or Update a Benutzer in Ki-TAX from an external login module. If the user is
	 * already found by its unique username we update the existing entry, otherwise we create a new one
	 *
	 * @param benutzer User to update/store
	 * @return stored object
	 */
	@POST
	@Path("/benutzer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	JaxExternalBenutzer updateOrStoreUserFromIAM(
		@Nonnull JaxExternalBenutzer benutzer
	);

	/**
	 * @return the first and only Mandant that currently exists
	 */
	@Nonnull
	@GET
	@Path("/mandant")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	String getMandant();

	/**
	 * This service exists to allow external login modules to create logins in Ki-Tax
	 *
	 * @param jaxExtAuthUser the login entry to create
	 * @return Object containing the information that is relevant for the Cookie
	 */
	@POST
	@Path("/extauth")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	JaxExternalAuthAccessElement createLoginFromIAM(
		@Nonnull JaxExternalAuthorisierterBenutzer jaxExtAuthUser);

}
