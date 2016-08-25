package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxFamiliensituation;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.FamiliensituationService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.WizardStepService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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
import java.util.Objects;
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
	private WizardStepService wizardStepService;
	@Inject
	private GesuchService gesuchService;

	@Inject
	private JaxBConverter converter;


	@ApiOperation(value = "Creates a new Familiensituation in the database. ")
	@Nullable
	@POST
	@Path("/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createFamiliensituation (
		@Nonnull @NotNull @PathParam ("gesuchId") JaxId gesuchJAXPId,
		@Nonnull @NotNull JaxFamiliensituation familiensituationJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchJAXPId.getId());
		if (gesuch.isPresent()) {
			Familiensituation convertedFamiliensituation = converter.familiensituationToEntity(familiensituationJAXP, new Familiensituation());
			Familiensituation persistedFamiliensituation = this.familiensituationService.createFamiliensituation(convertedFamiliensituation);
			gesuch.get().setFamiliensituation(persistedFamiliensituation);

			URI uri = uriInfo.getBaseUriBuilder()
				.path(FamiliensituationResource.class)
				.path("/" + persistedFamiliensituation.getId())
				.build();

			JaxFamiliensituation jaxFamilienSituation = converter.familiensituationToJAX(persistedFamiliensituation);

			wizardStepService.updateSteps(gesuchJAXPId.getId(), null,
				persistedFamiliensituation, WizardStepName.FAMILIENSITUATION);

			return Response.created(uri).entity(jaxFamilienSituation).build();
		}
		throw new EbeguEntityNotFoundException("createFamiliensituation", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchJAXPId.getId());
	}

	@Nullable
	@PUT
	@Path("/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxFamiliensituation updateFamiliensituation(
		@Nonnull @NotNull @PathParam ("gesuchId") JaxId gesuchJAXPId,
		@Nonnull @NotNull JaxFamiliensituation familiensituationJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Objects.requireNonNull(familiensituationJAXP.getId());

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchJAXPId.getId());
		if (gesuch.isPresent()) {
			Optional<Familiensituation> loadedFamiliensituation = this.familiensituationService.findFamiliensituation(familiensituationJAXP.getId());
			if (!loadedFamiliensituation.isPresent()) {
				throw new EbeguEntityNotFoundException("updateFamiliensituation", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, familiensituationJAXP.getId());
			}
			Familiensituation convertedFamiliensituation = converter.familiensituationToEntity(familiensituationJAXP, loadedFamiliensituation.get());
			Familiensituation persistedFamiliensituation = this.familiensituationService.updateFamiliensituation(convertedFamiliensituation);

			wizardStepService.updateSteps(gesuchJAXPId.getId(), loadedFamiliensituation.get(),
				persistedFamiliensituation, WizardStepName.FAMILIENSITUATION);

			return converter.familiensituationToJAX(persistedFamiliensituation);
		}
		throw new EbeguEntityNotFoundException("updateFamiliensituation", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchJAXPId.getId());
	}

}
