package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsteller;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchstellerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

/**
 * REST Resource fuer Gesuchsteller
 */
@Path("gesuchsteller")
@Stateless
@Api
public class GesuchstellerResource {

	@Inject
	private GesuchstellerService gesuchstellerService;
	@Inject
	private GesuchService gesuchService;

	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Updates a Gesuchsteller or creates it if it doesn't exist in the database. The transfer object also has a relation to adressen " +
		"(wohnadresse, umzugadresse, korrespondenzadresse) these are stored in the database as well. Note that wohnadresse and" +
		"umzugadresse are both stored as consecutive wohnadressen in the database")
	@Nullable
	@PUT
	@Path("/{gesuchId}/gsNumber/{gsNumber}/{umzug}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuchsteller saveGesuchsteller(
		@Nonnull @NotNull @PathParam ("gesuchId") JaxId gesuchJAXPId,
		@Nonnull @NotNull @PathParam ("gsNumber") Integer gsNumber,
		@Nonnull @NotNull @PathParam ("umzug") Boolean umzug,
		@Nonnull @NotNull @Valid JaxGesuchsteller gesuchstellerJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchJAXPId.getId());
		if (gesuch.isPresent()) {
			Gesuchsteller gesuchstellerToMerge = new Gesuchsteller();
			if (gesuchstellerJAXP.getId() != null) {
				Optional<Gesuchsteller> optional = gesuchstellerService.findGesuchsteller(gesuchstellerJAXP.getId());
				gesuchstellerToMerge = optional.orElse(new Gesuchsteller());
			}

			Gesuchsteller convertedGesuchsteller = converter.gesuchstellerToEntity(gesuchstellerJAXP, gesuchstellerToMerge);
			Gesuchsteller persistedGesuchsteller = this.gesuchstellerService.saveGesuchsteller(convertedGesuchsteller, gesuch.get(), gsNumber, umzug);

			return converter.gesuchstellerToJAX(persistedGesuchsteller);
		}
		throw new EbeguEntityNotFoundException("createGesuchsteller", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchJAXPId.getId());

	}


	@Nullable
	@GET
	@Path("/{gesuchstellerId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxGesuchsteller findGesuchsteller(
		@Nonnull @NotNull JaxId gesuchstellerJAXPId) throws EbeguException {

		Validate.notNull(gesuchstellerJAXPId.getId());
		String gesuchstellerID = converter.toEntityId(gesuchstellerJAXPId);
		Optional<Gesuchsteller> optional = gesuchstellerService.findGesuchsteller(gesuchstellerID);

		if (!optional.isPresent()) {
			return null;
		}
		Gesuchsteller gesuchstellerToReturn = optional.get();

		return converter.gesuchstellerToJAX(gesuchstellerToReturn);
	}

}
