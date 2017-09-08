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


	@GET
	@Path("/heartbeat")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN)
	String getHeartBeat();

	/**
	 * Service to read the single-sign-on url that ki-tax should send clients without login to
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
