package ch.dvbern.ebegu.api.resource.util;

import ch.dvbern.ebegu.enums.Land;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("lists")
@Stateless
public class ListResource {

	@Path("laender")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCountries(@Context HttpServletRequest request) {

		List<Land> laender = new ArrayList<>();
		for (final Land landIsoCode : Land.values()) {
			if (!(Land.NICHTANERKANNT == landIsoCode
				|| Land.UNBEKANNT == landIsoCode
				|| Land.STAATENLOS == landIsoCode)
				&& landIsoCode.isValid()) {
				laender.add(landIsoCode);
			}
		}

		return Response.ok(laender).build();
	}


}
