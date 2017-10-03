
package ch.dvbern.ebegu.api.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
		@HeaderParam("X-OpenAM-Password") String password,
		@HeaderParam("Content-Type") String contentType);

}

