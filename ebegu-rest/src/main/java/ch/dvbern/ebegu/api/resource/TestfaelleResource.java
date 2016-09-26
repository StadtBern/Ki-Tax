package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.services.GesuchService;
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

	@Inject
	private GesuchService gesuchService;


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
			createAndSaveGesuch(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList));
			return Response.ok("Fall Dagmar Waelti erstellt").build();
		} else if ("2".equals(fallid)) {
			createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList));
			return Response.ok("Fall Yvonne Feutz erstellt").build();
		} else if ("3".equals(fallid)) {
			createAndSaveGesuch(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList));
			return Response.ok("Fall Marcia Perreira erstellt").build();
		} else if ("4".equals(fallid)) {
			createAndSaveGesuch(new Testfall04_WaltherLaura(gesuchsperiode, institutionStammdatenList));
			return Response.ok("Fall Laura Walther erstellt").build();
		} else if ("5".equals(fallid)) {
			createAndSaveGesuch(new Testfall05_LuethiMeret(gesuchsperiode, institutionStammdatenList));
			return Response.ok("Fall Meret Luethi erstellt").build();
		} else if ("6".equals(fallid)) {
			createAndSaveGesuch(new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList));
			return Response.ok("Fall Nora Becker erstellt").build();
		} else if ("all".equals(fallid)) {
			createAndSaveGesuch(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList));
			createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList));
			createAndSaveGesuch(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList));
			createAndSaveGesuch(new Testfall04_WaltherLaura(gesuchsperiode, institutionStammdatenList));
			createAndSaveGesuch(new Testfall05_LuethiMeret(gesuchsperiode, institutionStammdatenList));
			createAndSaveGesuch(new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList));
			return Response.ok("Testfaelle 1-6 erstellt").build();
		} else {
			return Response.ok("Usage: /Nummer des Testfalls an die URL anhaengen. Bisher umgesetzt: 1-6. '/all' erstellt alle Testfaelle").build();
		}
	}

	private void createAndSaveGesuch(AbstractTestfall fromTestfall) {
		final Optional<List<Gesuch>> gesuchByGSName = gesuchService.findGesuchByGSName(fromTestfall.getNachname(), fromTestfall.getVorname());
		if (gesuchByGSName.isPresent()) {
			final List<Gesuch> gesuches = gesuchByGSName.get();
			if (!gesuches.isEmpty()) {
				fromTestfall.setFall(gesuches.iterator().next().getFall());
			}
		}

		Gesuch gesuch = fromTestfall.createGesuch();
		persistence.persist(gesuch.getFall());
		persistence.persist(gesuch);
		final List<WizardStep> wizardSteps = fromTestfall.createWizardSteps(gesuch);
		for (final WizardStep wizardStep : wizardSteps) {
			persistence.persist(wizardStep);
		}
	}
}
