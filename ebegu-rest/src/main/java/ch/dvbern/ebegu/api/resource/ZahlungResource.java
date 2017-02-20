package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxZahlungsauftrag;
import ch.dvbern.ebegu.entities.Zahlungsauftrag;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.ZahlungService;
import ch.dvbern.ebegu.util.DateUtil;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
	@GET
	@Path("/zahlungsauftrag/{zahlungsauftragId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxZahlungsauftrag findZahlungsauftrag(
		@Nonnull @NotNull @PathParam("zahlungsauftragId") JaxId zahlungsauftragJAXPId) throws EbeguException {

		Validate.notNull(zahlungsauftragJAXPId.getId());
		String zahlungsauftragId = converter.toEntityId(zahlungsauftragJAXPId);
		Optional<Zahlungsauftrag> optional = zahlungService.findZahlungsauftrag(zahlungsauftragId);

		if (!optional.isPresent()) {
			return null;
		}
		return converter.zahlungsauftragToJAX(optional.get());
	}


	@Nullable
	@GET
	@Path("/create")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxZahlungsauftrag createZahlung(
		@QueryParam("faelligkeitsdatum") String stringFaelligkeitsdatum,
		@QueryParam("beschrieb") String beschrieb,
		@Nullable @QueryParam("datumGeneriert") String stringDatumGeneriert) throws EbeguRuntimeException {

		LocalDate faelligkeitsdatum = DateUtil.parseStringToDateOrReturnNow(stringFaelligkeitsdatum);
		LocalDateTime datumGeneriert;
		if(stringDatumGeneriert!=null) {
			datumGeneriert = DateUtil.parseStringToDateOrReturnNow(stringDatumGeneriert).atStartOfDay();
		}else{
			datumGeneriert = LocalDateTime.now();
		}

		final Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(faelligkeitsdatum.atStartOfDay(), beschrieb, datumGeneriert);

		return converter.zahlungsauftragToJAX(zahlungsauftrag);
	}

	@Nullable
	@GET
	@Path("/update")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxZahlungsauftrag updateZahlung(
		@QueryParam("beschrieb") String beschrieb,
		@QueryParam("faelligkeitsdatum") String stringFaelligkeitsdatum,
		@QueryParam("id") String id) throws EbeguRuntimeException {

		LocalDate faelligkeitsdatum = DateUtil.parseStringToDateOrReturnNow(stringFaelligkeitsdatum);


		//TODO: Update no yet implemented!!!!!
		/*final Zahlungsauftrag zahlungsauftrag = zahlungService.zahlungsauftragErstellen(faelligkeitsdatum.atStartOfDay(), beschrieb, datumGeneriert);
		return converter.zahlungsauftragToJAX(zahlungsauftrag);*/

		final Zahlungsauftrag zahlungsauftrag = zahlungService.findZahlungsauftrag(id).get();
		zahlungsauftrag.setDatumFaellig(faelligkeitsdatum.atStartOfDay());
		zahlungsauftrag.setBeschrieb(beschrieb);
		return converter.zahlungsauftragToJAX(zahlungsauftrag);
	}
}
