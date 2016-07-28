package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;
import ch.dvbern.ebegu.testfaelle.AbstractTestfall;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.lib.cdipersistence.Persistence;
import io.swagger.annotations.Api;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * REST Resource zur Erstellung von (vordefinierten) Testfaellen
 */
@Path("testfaelle")
@Stateless
@Api
public class TestfaelleResource {

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private Persistence<AbstractEntity> persistence;

	@Inject
	private InstitutionStammdatenService institutionStammdatenService;


	@GET
	@Path("/testfall/{fallid}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	public Response getTestFall(@PathParam("fallid") String fallid) {
		Collection<Gesuchsperiode> allActiveGesuchsperioden = gesuchsperiodeService.getAllActiveGesuchsperioden();
		Gesuchsperiode gesuchsperiode = allActiveGesuchsperioden.iterator().next();
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		Optional<InstitutionStammdaten> optionalAaregg = institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.idInstitutionAaregg);
		Optional<InstitutionStammdaten> optionalBruennen = institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.idInstitutionBruennen);
		if (optionalAaregg.isPresent()) {
			institutionStammdatenList.add(optionalAaregg.get());
		}
		if (optionalBruennen.isPresent()) {
			institutionStammdatenList.add(optionalBruennen.get());
		}

		if ("1".equals(fallid)) {
			Testfall01_WaeltiDagmar test = new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList);
			Gesuch gesuch = test.createGesuch();
			persistence.persist(gesuch.getFall());
			persistence.persist(gesuch);
			return Response.ok("Fall Waelti Dagmar erstellt").build();
		}
		return Response.serverError().entity("Unknown fallID").build();
	}
}
