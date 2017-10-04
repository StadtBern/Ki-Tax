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

package ch.dvbern.ebegu.api.connector.clientinfo;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/info")
public interface ILoginProviderInfoResource {


	/**
	 * @return an Answerstring to test if the api is up and running, requires no password
	 */
	@GET
	@Path("/heartbeat")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	String getHeartBeat();

	/**
	 * Service to read the single-sign-on url that ki-tax should send clients without login to
	 * @return uri as string
	 */
	@GET
	@Path("/singleSingOnURL")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	String getSSOLoginInitURL(
		@Nullable @QueryParam("relayPath") String relayPath
	);

	/**
	 * Service to send browsers to when starting a single log out
	 */
	@GET
	@Path("/singleLogoutURL")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	String getSingleLogoutURL(
		@Nullable @QueryParam("relayPath") String relayPath,
		@Nullable @QueryParam("nameID") String nameID,
		@Nullable @QueryParam("sessionID") String sessionID
	);

}
