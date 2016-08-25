package ch.dvbern.ebegu.tests.rules;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.ebegu.testfaelle.Testfall02_FeutzYvonne;
import ch.dvbern.ebegu.testfaelle.Testfall03_PerreiraMarcia;
import ch.dvbern.ebegu.testfaelle.Testfall06_BeckerNora;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test der die vom JA gemeldeten Testfaelle ueberprueft.
 */
public class TestfaelleTest extends AbstractBGRechnerTest {

	@Test
	public void testfall01_WaeltiDagmar() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());
		checkTestfallWaeltiDagmar(gesuch);
	}


	@Test
	public void testfall02_FeutzYvonne() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenTagiAaregg());
		Testfall02_FeutzYvonne testfall = new Testfall02_FeutzYvonne(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());
		checkTestfallFeutzYvonne(gesuch);
	}

	@Test
	public void testfall02_PerreiraMarcia() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		Testfall03_PerreiraMarcia testfall = new Testfall03_PerreiraMarcia(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());
		checkTestfallPerreiraMarcia(gesuch);
	}

	@Test
	public void testfall06_BeckerNora() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenTagiAaregg());
		Testfall06_BeckerNora testfall = new Testfall06_BeckerNora(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());
		checkTestfallBeckerNora(gesuch);
	}

}
