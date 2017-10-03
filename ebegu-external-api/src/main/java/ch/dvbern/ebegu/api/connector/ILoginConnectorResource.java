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
