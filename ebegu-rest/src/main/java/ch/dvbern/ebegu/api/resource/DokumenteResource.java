package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxDokumente;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.rules.Anlageverzeichnis.DokumentenverzeichnisEvaluator;
import ch.dvbern.ebegu.services.GesuchService;
import io.swagger.annotations.Api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;
import java.util.Set;

/**
 * REST Resource fuer FinanzielleSituation
 */
@Path("dokumente")
@Stateless
@Api
public class DokumenteResource {

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private JaxBConverter converter;

	@Resource
	private EJBContext context;    //fuer rollback

	@Inject
	private GesuchService gesuchService;

	@Inject
	DokumentenverzeichnisEvaluator dokumentenverzeichnisEvaluator;

	@Nullable
	@GET
	@Path("/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxDokumente getDokumente(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchId) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchId.getId());
		if (gesuch.isPresent()) {

			final Set<DokumentGrund> dokumentGrunds = dokumentenverzeichnisEvaluator.calculate(gesuch.get());

			// Wir wollen nur neu berechnen. Das Gesuch soll auf keinen Fall neu gespeichert werden
			//context.setRollbackOnly();
			return converter.dokumentGruendeToJAX(dokumentGrunds);
		}
		throw new EbeguEntityNotFoundException("getDokumente", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchId.getId());

	}
}
