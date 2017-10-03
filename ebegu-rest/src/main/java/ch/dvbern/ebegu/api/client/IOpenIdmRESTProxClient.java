
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

