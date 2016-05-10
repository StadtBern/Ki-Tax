package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAdresse;
import ch.dvbern.ebegu.entities.PersonenAdresse;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.AdresseService;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;

/**
 * Created by imanol on 17.03.16.
 *
 * REST Resource fuer Adressen
 */
@Path("adressen")
@Stateless
@Api
public class AdresseResource {

	@Inject
	private AdresseService adresseService;

	@Inject
	private JaxBConverter converter;


	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
		@Nonnull @NotNull JaxAdresse adresseJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		PersonenAdresse convertedPersonenAdresse = converter.adresseToEntity(adresseJAXP, new PersonenAdresse());
		PersonenAdresse persistedPersonenAdresse = this.adresseService.createAdresse(convertedPersonenAdresse);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(AdresseResource.class)
			.path("/" + persistedPersonenAdresse.getId())
			.build();

		return Response.created(uri).entity(converter.adresseToJAX(persistedPersonenAdresse)).build();

	}

	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxAdresse update(
		@Nonnull @NotNull JaxAdresse adresseJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(adresseJAXP.getId());
		Optional<PersonenAdresse> adrFromDB = adresseService.findAdresse(adresseJAXP.getId());
		PersonenAdresse adrToMerge = converter.adresseToEntity(adresseJAXP, adrFromDB.get());
		PersonenAdresse modifiedPersonenAdresse = this.adresseService.updateAdresse(adrToMerge);

		return converter.adresseToJAX(modifiedPersonenAdresse);
	}

}
