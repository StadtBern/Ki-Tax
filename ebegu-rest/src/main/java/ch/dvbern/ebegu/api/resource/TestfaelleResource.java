package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.lib.cdipersistence.Persistence;
import io.swagger.annotations.Api;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.Collection;

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
		Collection<InstitutionStammdaten> allInstitutionStammdatenByDate = institutionStammdatenService.getAllInstitutionStammdatenByDate(LocalDate.now());

		if ("1".equals(fallid)) {
			Testfall01_WaeltiDagmar test = new Testfall01_WaeltiDagmar(gesuchsperiode, allInstitutionStammdatenByDate);
			Gesuch gesuch = test.createGesuch();
			persistence.persist(gesuch.getFall());
			persistence.persist(gesuch);
			return Response.ok("Fall Waelti Dagmar erstellt").build();
		}
		return Response.serverError().build();
	}
}
