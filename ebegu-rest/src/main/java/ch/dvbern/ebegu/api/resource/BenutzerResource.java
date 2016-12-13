package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAuthLoginElement;
import ch.dvbern.ebegu.services.BenutzerService;
import io.swagger.annotations.Api;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Resource fuer Benutzer  (Auf client userRS.rest.ts also eingetlich die UserResources)
 */
@Path("benutzer")
@Stateless
@Api
public class BenutzerResource {

	@Inject
	private BenutzerService  benutzerService;

	@Inject
	private JaxBConverter converter;

	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxAuthLoginElement> getAllUsers() {
		return benutzerService.getAllBenutzer().stream()
			.map(benutzer -> converter.benutzerToAuthLoginElement(benutzer))
			.collect(Collectors.toList());
	}

	/**
	 * nur Benutzer Sachbearbeiter_JA oder Admin
	 */
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
	/**
	 * nur Gesuchsteller
	 */
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
