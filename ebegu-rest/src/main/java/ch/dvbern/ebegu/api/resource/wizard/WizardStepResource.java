package ch.dvbern.ebegu.api.resource.wizard;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxWizardStep;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.WizardStep;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.WizardStepService;
import io.swagger.annotations.Api;
import org.apache.commons.lang.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Resource fuer Wizardsteps
 */
@Path("wizard-steps")
@Stateless
@Api()
public class WizardStepResource {

	@Inject
	private GesuchService gesuchService;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private JaxBConverter converter;


	@GET
	@Path("/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<JaxWizardStep> findWizardStepsFromGesuch(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchJAXPId) throws EbeguException {
		Validate.notNull(gesuchJAXPId.getId());

		final String gesuchID = converter.toEntityId(gesuchJAXPId);

		return wizardStepService.findWizardStepsFromGesuch(gesuchID).stream()
			.map(wizardStep -> converter.wizardStepToJAX(wizardStep)).collect(Collectors.toList());
	}

	/**
	 * Creates all required WizardSteps for the given Gesuch and returns them as a List. Status for all Steps will be UNBESUCHT except for
	 * GESUCH_ERSTELLEN, which gets OK, because this step is already done when the gesuch is created.
	 * @param gesuchJAXPId
	 * @return
	 * @throws EbeguException
	 */
	@Nullable
	@GET
	@Path("/createWizardSteps/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxWizardStep> createWizardStepList(
		@Nonnull @NotNull @PathParam ("gesuchId") JaxId gesuchJAXPId) throws EbeguException {
		Validate.notNull(gesuchJAXPId.getId());

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchJAXPId.getId());
		if (gesuch.isPresent()) {
			return wizardStepService.createWizardStepList(gesuch.get()).stream()
				.map(wizardStep -> converter.wizardStepToJAX(wizardStep)).collect(Collectors.toList());
		}
		return null;
	}

	@Nullable
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxWizardStep saveWizardStep(
		@Nonnull @NotNull @Valid JaxWizardStep wizardStepJAXP) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(wizardStepJAXP.getGesuchId());
		if (gesuch.isPresent()) {
			WizardStep wizardStepToMerge = new WizardStep();
			if (wizardStepJAXP.getId() != null) {
				Optional<WizardStep> optional = wizardStepService.findWizardStep(wizardStepJAXP.getId());
				wizardStepToMerge = optional.orElse(new WizardStep());
			}
			WizardStep convertedWizardStep = converter.wizardStepToEntity(wizardStepJAXP, wizardStepToMerge);
			convertedWizardStep.setGesuch(gesuch.get());
			WizardStep persistedWizardStep = this.wizardStepService.saveWizardStep(convertedWizardStep);
			return converter.wizardStepToJAX(persistedWizardStep);
		}
		throw new EbeguEntityNotFoundException("saveWizardStep", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + wizardStepJAXP.getGesuchId());
	}

}
