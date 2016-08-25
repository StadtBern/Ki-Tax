package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;
import ch.dvbern.ebegu.testfaelle.*;
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
		Optional<InstitutionStammdaten> optionalTagiAaregg = institutionStammdatenService.findInstitutionStammdaten("c10405d6-a905-4879-bb38-fca4cbb3f06f");
		if (optionalAaregg.isPresent()) {
			institutionStammdatenList.add(optionalAaregg.get());
		}
		if (optionalBruennen.isPresent()) {
			institutionStammdatenList.add(optionalBruennen.get());
		}
		if (optionalTagiAaregg.isPresent()) {
			institutionStammdatenList.add(optionalTagiAaregg.get());
		}
		if ("1".equals(fallid)) {
			Testfall01_WaeltiDagmar test = new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList);
			Gesuch gesuch = test.createGesuch();
			persistence.persist(gesuch.getFall());
			persistence.persist(gesuch);
			return Response.ok("Fall Waelti Dagmar erstellt").build();
		} else if ("2".equals(fallid)) {
			Testfall02_FeutzYvonne test = new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList);
			Gesuch gesuch = test.createGesuch();
			persistence.persist(gesuch.getFall());
			persistence.persist(gesuch);
			return Response.ok("Fall Yvonne Feutz erstellt").build();
		} else if ("3".equals(fallid)) {
			Testfall03_PerreiraMarcia test = new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList);
			Gesuch gesuch = test.createGesuch();
			persistence.persist(gesuch.getFall());
			persistence.persist(gesuch);
			return Response.ok("Fall Marcia Perreira erstellt").build();
		} else if ("6".equals(fallid)) {
			Testfall06_BeckerNora test = new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList);
			Gesuch gesuch = test.createGesuch();
			persistence.persist(gesuch.getFall());
			persistence.persist(gesuch);
			return Response.ok("Fall Nora Becker erstellt").build();
		}
		return Response.serverError().entity("Unknown fallID").build();
	}
}
