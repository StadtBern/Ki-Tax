package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxAntragStatusHistory;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.AntragStatusHistory;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.AntragStatusHistoryService;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Optional;

/**
 * REST Resource fuer die History von Gesuchen/Mutationen (Antraegen)
 */
@Path("antragStatusHistory")
@Stateless
@Api(description = "Resource zum verwalten von Kindern eines Gesuchstellers")
public class AntragStatusHistoryResource {

	@Inject
	private JaxBConverter converter;
	@Inject
	private GesuchService gesuchService;
	@Inject
	private GesuchsperiodeService gesuchsperiodeService;
	@Inject
	private FallService fallService;
	@Inject
	private AntragStatusHistoryService antragStatusHistoryService;


	@Nullable
	@GET
	@Path("/{gesuchId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxAntragStatusHistory findLastStatusChange(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId jaxGesuchId) throws EbeguException {

		Validate.notNull(jaxGesuchId.getId());
		String gesuchId = converter.toEntityId(jaxGesuchId);
		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchId);

		if (gesuch.isPresent()) {
			final AntragStatusHistory lastStatusChange = antragStatusHistoryService.findLastStatusChange(gesuch.get());
			if (lastStatusChange != null) {
				return converter.antragStatusHistoryToJAX(lastStatusChange);
			}
		}
		return null;
	}

	@Nullable
	@GET
	@Path("/verlauf/{gesuchsperiodeId}/{fallId}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<JaxAntragStatusHistory> findAllAntragStatusHistoryByGesuch(
		@Nonnull @NotNull @PathParam("gesuchsperiodeId") JaxId jaxGesuchsperiodeId,
		@Nonnull @NotNull @PathParam("fallId") JaxId jaxFallId) throws EbeguException {

		Validate.notNull(jaxGesuchsperiodeId.getId());
		String gesuchsperiodeId = converter.toEntityId(jaxGesuchsperiodeId);
		Validate.notNull(jaxFallId.getId());
		String fallId = converter.toEntityId(jaxFallId);

		Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(gesuchsperiodeId);
		if (!gesuchsperiode.isPresent()) {
			throw new EbeguEntityNotFoundException("findAllAntragStatusHistoryByGesuch", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, jaxGesuchsperiodeId.getId());
		}
		Optional<Fall> fall = fallService.findFall(fallId);
		if (!fall.isPresent()) {
			throw new EbeguEntityNotFoundException("findAllAntragStatusHistoryByGesuch", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, jaxFallId.getId());
		}

        final Collection<AntragStatusHistory> statusHistory = antragStatusHistoryService.findAllAntragStatusHistoryByGPFall(gesuchsperiode.get(), fall.get());
		return converter.antragStatusHistoryCollectionToJAX(statusHistory);
	}
}
