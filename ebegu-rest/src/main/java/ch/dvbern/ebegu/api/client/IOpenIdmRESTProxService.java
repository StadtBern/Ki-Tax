
package ch.dvbern.ebegu.api.client;

import io.swagger.jaxrs.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Dies das proxy interface fuer den HTTP Endpunkt des openidm
 */
public interface IOpenIdmRESTProxService {


	@GET
	@Path("/openidm/info/login")
	@Consumes(MediaType.APPLICATION_JSON)
	Response login(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@HeaderParam("X-OpenIDM-NoSession") boolean noSession);

	@GET
	@Path("/openidm/managed/institution")
	@Consumes(MediaType.APPLICATION_JSON)
	Response getAllInstitutions(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@HeaderParam("X-OpenIDM-NoSession") boolean noSession,
		@QueryParam("_queryFilter") boolean queryFilter);

	@GET
	@Path("/openidm/managed/institution/{uid}")
	@Consumes(MediaType.APPLICATION_JSON)
	Response getInstitutionbyUid(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@HeaderParam("X-OpenIDM-NoSession") boolean noSession,
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

	@DELETE
	@Path("/openidm/managed/institution/{uid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response delete(
		@HeaderParam("X-OpenIDM-Username") String username,
		@HeaderParam("X-OpenIDM-Password") String password,
		@PathParam("uid") String uid);
}

