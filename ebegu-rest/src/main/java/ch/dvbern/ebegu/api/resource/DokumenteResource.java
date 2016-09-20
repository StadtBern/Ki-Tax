package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxDokument;
import ch.dvbern.ebegu.api.dtos.JaxDokumentGrund;
import ch.dvbern.ebegu.api.dtos.JaxDokumente;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rules.Anlageverzeichnis.DokumentenverzeichnisEvaluator;
import ch.dvbern.ebegu.services.DokumentGrundService;
import ch.dvbern.ebegu.services.FileSaverService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.WizardStepService;
import ch.dvbern.ebegu.util.DokumenteUtil;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * REST Resource fuer Dokumente
 */
@Path("dokumente")
@Stateless
@Api
public class DokumenteResource {

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private JaxBConverter converter;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private DokumentenverzeichnisEvaluator dokumentenverzeichnisEvaluator;

	@Inject
	private DokumentGrundService dokumentGrundService;
	@Inject
	private WizardStepService wizardStepService;

	@Inject
	private FileSaverService fileSaverService;


	@Nullable
	@GET
	@Path("/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxDokumente getDokumente(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchId) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchId.getId());
		if (gesuch.isPresent()) {

			final Set<DokumentGrund> dokumentGrundsNeeded = dokumentenverzeichnisEvaluator.calculate(gesuch.get());
			dokumentenverzeichnisEvaluator.addSonstige(dokumentGrundsNeeded);
			dokumentenverzeichnisEvaluator.addPapiergesuch(dokumentGrundsNeeded, gesuch.get());

			final Collection<DokumentGrund> persistedDokumentGrund = dokumentGrundService.getAllDokumentGrundByGesuch(gesuch.get());

			final Set<DokumentGrund> dokumentGrundsMerged = DokumenteUtil.mergeNeededAndPersisted(dokumentGrundsNeeded, persistedDokumentGrund);

			return converter.dokumentGruendeToJAX(dokumentGrundsMerged);
		}
		throw new EbeguEntityNotFoundException("getDokumente", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchId.getId());
	}

	@Nullable
	@GET
	@Path("/byTyp/{gesuchId}/{dokumentGrundTyp}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxDokumente getDokumente(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchId,
		@Nonnull @NotNull @PathParam("dokumentGrundTyp") DokumentGrundTyp dokumentGrundTyp) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchId.getId());
		if (gesuch.isPresent()) {

			final Set<DokumentGrund> dokumentGrundsNeeded = new HashSet<>();

			dokumentenverzeichnisEvaluator.addPapiergesuch(dokumentGrundsNeeded, gesuch.get());

			final Collection<DokumentGrund> persistedDokumentGrund = dokumentGrundService.getAllDokumentGrundByGesuchAndDokumentType(gesuch.get(), dokumentGrundTyp);

			final Set<DokumentGrund> dokumentGrundsMerged = DokumenteUtil.mergeNeededAndPersisted(dokumentGrundsNeeded, persistedDokumentGrund);

			return converter.dokumentGruendeToJAX(dokumentGrundsMerged);
		}
		throw new EbeguEntityNotFoundException("getDokumente", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchId.getId());
	}


	@ApiOperation(value = "Update a Institution in the database.")
	@Nullable
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxDokumentGrund updateDokumentGrund(
		@Nonnull @NotNull @Valid JaxDokumentGrund dokumentGrundJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Validate.notNull(dokumentGrundJAXP.getId());
		Optional<DokumentGrund> dokumentGrundOptional = dokumentGrundService.findDokumentGrund(dokumentGrundJAXP.getId());
		DokumentGrund dokumentGrundFromDB = dokumentGrundOptional.orElseThrow(() -> new EbeguEntityNotFoundException("update", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, dokumentGrundJAXP.getId()));

		// Files where no in the list anymore, should be deleted on Filesystem!
		Set<Dokument> dokumentsToRemove = findDokumentToRemove(dokumentGrundJAXP, dokumentGrundFromDB);
		for (Dokument dokument : dokumentsToRemove) {
			fileSaverService.remove(dokument.getFilepfad());
		}

		DokumentGrund dokumentGrundToMerge = converter.dokumentGrundToEntity(dokumentGrundJAXP, dokumentGrundFromDB);
		DokumentGrund modifiedDokumentGrund = this.dokumentGrundService.updateDokumentGrund(dokumentGrundToMerge);

		if (modifiedDokumentGrund == null) {
			return null;
		}

		wizardStepService.updateSteps(modifiedDokumentGrund.getGesuch().getId(), null, null, WizardStepName.DOKUMENTE);
		return converter.dokumentGrundToJax(modifiedDokumentGrund);

	}

	/**
	 * Gibt Liste von Dokumenten zurück, welche auf der DB vorhanden sind auf dem jaxB Objekt jedoch nicht mehr.
	 */
	private Set<Dokument> findDokumentToRemove(JaxDokumentGrund dokumentGrundJAXP, DokumentGrund dokumentGrundFromDB) {
		Set<Dokument> dokumentsToRemove = new HashSet<Dokument>();
		Validate.notNull(dokumentGrundFromDB.getDokumente());
		Validate.notNull(dokumentGrundJAXP.getDokumente());

		for (Dokument dokument : dokumentGrundFromDB.getDokumente()) {
			boolean found = false;
			for (JaxDokument jaxDokument : dokumentGrundJAXP.getDokumente()) {
				if (dokument.getId().equals(jaxDokument.getId())) {
					found = true;
					break;
				}
			}
			if (!found) {
				dokumentsToRemove.add(dokument);
			}
		}
		return dokumentsToRemove;
	}

}
