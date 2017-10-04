
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

package ch.dvbern.ebegu.api.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Dies das proxy interface fuer den HTTP Endpunkt des openidm
 */
public interface IOpenIdmRESTProxClient {

	@GET
	@Path("/openidm/managed/institution")
	@Consumes(MediaType.APPLICATION_JSON)
	Response getAllInstitutions(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@HeaderParam("X-OpenIDM-NoSession") boolean noSession,
		@QueryParam("_queryFilter") boolean queryFilter);

	@GET
	@Path("/openidm/managed/institution")
	@Consumes(MediaType.APPLICATION_JSON)
	Response getAllInstitutionsWithToken(
		@HeaderParam("egovBernChCookie") String tokenId,
		@HeaderParam("X-Requested-With") String XMLHttpRequest,
		@HeaderParam("Content-Type") String contentType,
		@QueryParam("_queryFilter") boolean queryFilter);

	@GET
	@Path("/openidm/managed/institution/{uid}")
	@Consumes(MediaType.APPLICATION_JSON)
	Response getInstitutionbyUid(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@HeaderParam("X-OpenIDM-NoSession") boolean noSession,
		@PathParam("uid") String uid);

	@GET
	@Path("/openidm/managed/institution/{uid}")
	@Consumes(MediaType.APPLICATION_JSON)
	Response getInstitutionbyUidWithToken(
		@HeaderParam("egovBernChCookie") String tokenId,
		@HeaderParam("X-Requested-With") String XMLHttpRequest,
		@HeaderParam("Content-Type") String contentType,
		@PathParam("uid") String uid);

	@PUT
	@Path("/openidm/managed/institution/{uid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response create(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@PathParam("uid") String uid,
		JaxInstitutionOpenIdm jaxInstitutionOpenIdm);

	@PUT
	@Path("/openidm/managed/institution/{uid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response createWithToken(
		@HeaderParam("egovBernChCookie") String tokenId,
		@HeaderParam("X-Requested-With") String XMLHttpRequest,
		@HeaderParam("Content-Type") String contentType,
		@PathParam("uid") String uid,
		JaxInstitutionOpenIdm jaxInstitutionOpenIdm);

	@DELETE
	@Path("/openidm/managed/institution/{uid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response delete(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@PathParam("uid") String uid);

	@DELETE
	@Path("/openidm/managed/institution/{uid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response deleteWithToken(
		@HeaderParam("egovBernChCookie") String tokenId,
		@HeaderParam("X-Requested-With") String XMLHttpRequest,
		@HeaderParam("Content-Type") String contentType,
		@PathParam("uid") String uid);
}

