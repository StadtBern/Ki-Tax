package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxMandant;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.MandantService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

/**
 * REST Resource fuer Mandanten
 */
@Path("mandanten")
@Stateless
@Api
public class MandantResource {

	@Inject
	private MandantService mandantService;

	@Inject
	private JaxBConverter converter;

	@Nullable
	@GET
	@Path("/{mandantId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxMandant findMandant(@Nonnull @NotNull @PathParam("mandantId") JaxId mandantJAXPId) throws EbeguException {
		Validate.notNull(mandantJAXPId.getId());
		String mandantID = converter.toEntityId(mandantJAXPId);
		Optional<Mandant> optional = mandantService.findMandant(mandantID);

		if (!optional.isPresent()) {
			return null;
		}
		return converter.mandantToJAX(optional.get());
	}

}
