
package ch.dvbern.ebegu.api.client;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Dies das proxy interface fuer den HTTP Endpunkt des openam
 */
public interface IOpenAmRESTProxClient {

	@POST
	@Path("/am/json/authenticate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response login(
		@HeaderParam("X-OpenAM-Username") String username,
		@HeaderParam("X-OpenAM-Password") String password);

}

