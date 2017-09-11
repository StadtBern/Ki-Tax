package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAdresse;
import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.AdresseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

/**
 * REST Resource fuer Adressen
 */
@Path("adressen")
@Stateless
@Api(description = "Resource zum Speichern von Adressen")
public class AdresseResource {

	@Inject
	private AdresseService adresseService;

	@Inject
	private JaxBConverter converter;

	@ApiOperation(value = "Erstellt eine neue Adresse in der Datenbank.", response = JaxAdresse.class)
	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
		@Nonnull @NotNull JaxAdresse adresseJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Adresse convertedAdresse = converter.adresseToEntity(adresseJAXP, new Adresse());
		Adresse persistedAdresse = this.adresseService.createAdresse(convertedAdresse);

		URI uri = uriInfo.getBaseUriBuilder()
			.path(AdresseResource.class)
			.path("/" + persistedAdresse.getId())
			.build();

		return Response.created(uri).entity(converter.adresseToJAX(persistedAdresse)).build();
	}

	@ApiOperation(value = "Aktualisiert eine Adresse in der Datenbank.", response = JaxAdresse.class)
	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxAdresse update(
		@Nonnull @NotNull JaxAdresse adresseJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(adresseJAXP.getId());
		Adresse adrFromDB = adresseService.findAdresse(adresseJAXP.getId()).orElseThrow(() -> new EbeguEntityNotFoundException("update", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, adresseJAXP.getId()));
		Adresse adrToMerge = converter.adresseToEntity(adresseJAXP, adrFromDB);
		Adresse modifiedAdresse = this.adresseService.updateAdresse(adrToMerge);

		return converter.adresseToJAX(modifiedAdresse);
	}
}
