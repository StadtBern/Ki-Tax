package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxZahlungsauftrag;
import ch.dvbern.ebegu.entities.Zahlungsauftrag;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.ZahlungService;
import io.swagger.annotations.Api;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * resource fuer Zahlungen
 */
@Path("zahlungen")
@Stateless
@Api(description = "Resource zum verwalten von Zahlungen")
public class ZahlungResource {


	@Inject
	private ZahlungService zahlungService;

	@Inject
	private JaxBConverter converter;


	@Nullable
	@GET
	@Path("/all")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxZahlungsauftrag> getAllZahlungsauftraege() {
		return zahlungService.getAllZahlungsauftraege().stream()
			.map(zahlungsauftrag -> converter.zahlungsauftragToJAX(zahlungsauftrag))
			.collect(Collectors.toList());
	}


	@Nullable
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxZahlungsauftrag createZahlung(
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		final Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(LocalDateTime.now().plusYears(1), "TEST1232", LocalDateTime.now().plusYears(1).plusWeeks(1));

		return converter.zahlungsauftragToJAX(zahlungsauftrag);
	}
}
