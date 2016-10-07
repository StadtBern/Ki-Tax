package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;
import ch.dvbern.ebegu.testfaelle.*;
import ch.dvbern.lib.cdipersistence.Persistence;
import io.swagger.annotations.Api;

import javax.annotation.Nonnull;
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
 * REST Resource zur Erstellung von (vordefinierten) Testfaellen.
 * Alle Testfaelle erstellen:
 * http://localhost:8080/ebegu/api/v1/testfaelle/testfall/all
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
		return this.getTestFall(fallid, 1);
	}

	@GET
	@Path("/testfall/{fallid}/{iterationCount}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	public Response getTestFall(@PathParam("fallid") String fallid, @PathParam("iterationCount") Integer iterationCount) {
		iterationCount = iterationCount == null || iterationCount == 0 ? 1 : iterationCount;
		Collection<Gesuchsperiode> allActiveGesuchsperioden = gesuchsperiodeService.getAllActiveGesuchsperioden();
		Gesuchsperiode gesuchsperiode = allActiveGesuchsperioden.iterator().next();
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		Optional<InstitutionStammdaten> optionalAaregg = institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_AAREGG);
		Optional<InstitutionStammdaten> optionalBruennen = institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_BRUENNEN);
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

		StringBuilder responseString = createAndSaveTestfaelle(fallid, iterationCount, gesuchsperiode, institutionStammdatenList);
		return Response.ok(responseString.toString()).build();
	}

	@Nonnull
	private StringBuilder createAndSaveTestfaelle(String fallid, Integer iterationCount, Gesuchsperiode gesuchsperiode, List<InstitutionStammdaten> institutionStammdatenList) {
		StringBuilder responseString = new StringBuilder("");
		for (int i = 0; i < iterationCount; i++) {

			if ("1".equals(fallid)) {
				createAndSaveGesuch(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList));
				responseString.append("Fall Dagmar Waelti erstellt");
			} else if ("2".equals(fallid)) {
				createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList));
				responseString.append("Fall Yvonne Feutz erstellt");
			} else if ("3".equals(fallid)) {
				createAndSaveGesuch(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList));
				responseString.append("Fall Marcia Perreira erstellt");
			} else if ("4".equals(fallid)) {
				createAndSaveGesuch(new Testfall04_WaltherLaura(gesuchsperiode, institutionStammdatenList));
				responseString.append("Fall Laura Walther erstellt");
			} else if ("5".equals(fallid)) {
				createAndSaveGesuch(new Testfall05_LuethiMeret(gesuchsperiode, institutionStammdatenList));
				responseString.append("Fall Meret Luethi erstellt");
			} else if ("6".equals(fallid)) {
				createAndSaveGesuch(new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList));
				responseString.append("Fall Nora Becker erstellt");
			} else if ("all".equals(fallid)) {
				createAndSaveGesuch(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList));
				createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList));
				createAndSaveGesuch(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList));
				createAndSaveGesuch(new Testfall04_WaltherLaura(gesuchsperiode, institutionStammdatenList));
				createAndSaveGesuch(new Testfall05_LuethiMeret(gesuchsperiode, institutionStammdatenList));
				createAndSaveGesuch(new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList));
				responseString.append("Testfaelle 1-6 erstellt");
			} else {
				responseString.append("Usage: /Nummer des Testfalls an die URL anhaengen. Bisher umgesetzt: 1-6. '/all' erstellt alle Testfaelle");
			}

		}
		return responseString;
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
