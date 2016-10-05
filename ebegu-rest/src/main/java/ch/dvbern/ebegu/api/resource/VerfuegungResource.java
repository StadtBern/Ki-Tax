package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxKindContainer;
import ch.dvbern.ebegu.api.dtos.JaxVerfuegung;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.BetreuungService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.VerfuegungService;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.lib.cdipersistence.Persistence;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * REST Resource fuer FinanzielleSituation
 */
@Path("verfuegung")
@Stateless
@Api
public class VerfuegungResource {

	@Inject
	private VerfuegungService verfuegungService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private BetreuungService betreuungService;

	@Inject
	private InstitutionService institutionService;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private JaxBConverter converter;

	@Resource
	private EJBContext context;    //fuer rollback

	@Inject
	private Persistence<AbstractEntity> persistence;

	private static final Logger LOG = LoggerFactory.getLogger(this.getClass().getSimpleName());


	@ApiOperation(value = "Calculates the Verfuegung of the Gesuch with the given id, does nothing if the Gesuch does not exists. Note: Nothing is stored in the Databse")
	@Nullable
	@GET
	@Path("/calculate/{gesuchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response calculateVerfuegung(
		@Nonnull @NotNull @PathParam("gesuchId") JaxId gesuchstellerId,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(gesuchstellerId.getId());

		try {
		if (!gesuchOptional.isPresent()) {
			return null;
		}
		Gesuch gesuch = gesuchOptional.get();
		Gesuch gesuchWithCalcVerfuegung = verfuegungService.calculateVerfuegung(gesuch);


			// Wir verwenden das Gesuch nur zur Berechnung und wollen nicht speichern, darum das Gesuch detachen
			loadBetreuungspensumContainerAndDetachBetreuung(gesuchWithCalcVerfuegung);


			JaxGesuch gesuchJax = converter.gesuchToJAX(gesuchWithCalcVerfuegung);

		Collection<Institution> instForCurrBenutzer = institutionService.getInstitutionenForCurrentBenutzer();

		Set<JaxKindContainer> kindContainers = gesuchJax.getKindContainers();
		if (instForCurrBenutzer.size() > 0) {
			RestUtil.purgeKinderAndBetreuungenOfInstitutionen(kindContainers, instForCurrBenutzer);
		}

		return Response.ok(kindContainers).build();

		} finally {
			// Wir verwenden das Gesuch nur zur Berechnung und wollen nicht speichern, darum Transaktion rollbacken
			// Dies muss insbesondere auch im Fehlerfall geschehen!
			try {
				context.setRollbackOnly();
			} catch (IllegalStateException ise) {
				LOG.error("Could not rollback Transaction!", ise);
			}
		}
	}

	@Nullable
	@PUT
	@Path("/{gesuchId}/{betreuungId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxVerfuegung saveVerfuegung(
		@Nonnull @NotNull @PathParam ("gesuchId") JaxId gesuchId,
		@Nonnull @NotNull @PathParam ("betreuungId") JaxId betreuungId,
		@Nonnull @NotNull @Valid JaxVerfuegung verfuegungJAXP) throws EbeguException {

		Optional<Gesuch> gesuch = gesuchService.findGesuch(gesuchId.getId());
		if (gesuch.isPresent()) {
			Optional<Betreuung> betreuung = betreuungService.findBetreuung(betreuungId.getId());
			if (betreuung.isPresent()) {
				Verfuegung verfuegungToMerge = new Verfuegung();
				if (verfuegungJAXP.getId() != null) {
					Optional<Verfuegung> optional = verfuegungService.findVerfuegung(verfuegungJAXP.getId());
					verfuegungToMerge = optional.orElse(new Verfuegung());
				}
				Verfuegung convertedVerfuegung = converter.verfuegungToEntity(verfuegungJAXP, verfuegungToMerge);

				Verfuegung persistedVerfuegung = this.verfuegungService.saveVerfuegung(convertedVerfuegung, betreuung.get());

				return converter.verfuegungToJax(persistedVerfuegung);
			}
			throw new EbeguEntityNotFoundException("saveVerfuegung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "BetreuungID invalid: " + betreuungId.getId());
		}
		throw new EbeguEntityNotFoundException("saveVerfuegung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "GesuchId invalid: " + gesuchId.getId());
	}

	private void loadBetreuungspensumContainerAndDetachBetreuung(Gesuch gesuch) {
		for (Betreuung betreuung : gesuch.extractAllBetreuungen()) {
			betreuung.getBetreuungspensumContainers().size();
		}
		persistence.getEntityManager().detach(gesuch);
	}
}

