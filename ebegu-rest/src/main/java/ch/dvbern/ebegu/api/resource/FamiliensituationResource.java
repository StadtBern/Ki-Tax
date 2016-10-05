package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxFamiliensituation;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.FamiliensituationService;
import ch.dvbern.ebegu.services.GesuchService;
import io.swagger.annotations.Api;

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
import java.util.Optional;

/**
 * Resource fuer Familiensituation
 */
@Path("familiensituation")
@Stateless
@Api
public class FamiliensituationResource {

	@Inject
	private FamiliensituationService familiensituationService;
	@Inject
	private GesuchService gesuchService;

	@Inject
	private JaxBConverter converter;


	@Nullable
	@PUT
	@Path("/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFamiliensituation saveFamiliensituation(
		@Nonnull @NotNull @PathParam ("gesuchId") JaxId gesuchJAXPId,
		@Nonnull @NotNull JaxFamiliensituation familiensituationJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchJAXPId.getId());
		if (gesuch.isPresent()) {
			Familiensituation oldData = new Familiensituation();
			Familiensituation familiensituationToMerge = new Familiensituation();
			if (familiensituationJAXP.getId() != null) {
				Optional<Familiensituation> loadedFamiliensituation = this.familiensituationService.findFamiliensituation(familiensituationJAXP.getId());
				familiensituationToMerge = loadedFamiliensituation.orElse(new Familiensituation());
			}
			Familiensituation convertedFamiliensituation = converter.familiensituationToEntity(familiensituationJAXP, familiensituationToMerge);
			Familiensituation persistedFamiliensituation = this.familiensituationService.saveFamiliensituation(gesuch.get(), oldData, convertedFamiliensituation);

			return converter.familiensituationToJAX(persistedFamiliensituation);
		}
		throw new EbeguEntityNotFoundException("updateFamiliensituation", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchJAXPId.getId());
	}

}
