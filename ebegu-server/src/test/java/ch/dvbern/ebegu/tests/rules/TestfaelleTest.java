package ch.dvbern.ebegu.tests.rules;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.testfaelle.*;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test der die vom JA gemeldeten Testfaelle ueberprueft.
 */
public class TestfaelleTest extends AbstractBGRechnerTest {

	@Test
	public void indexOfTest() {
		String deployuri = "/ebegu/index.jsp";
		int slashLoc = deployuri.indexOf("/", 1);
		if (slashLoc != -1) {
			deployuri = deployuri.substring(0, slashLoc);
		}
	}

	@Test
	public void testfall01_WaeltiDagmar() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);

		testfall.createFall(null);
		testfall.createGesuch(null);
		Gesuch gesuch = testfall.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());
		checkTestfall01WaeltiDagmar(gesuch);
	}

	@Test
	public void testfall02_FeutzYvonne() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenTagiWeissenstein());
		Testfall02_FeutzYvonne testfall = new Testfall02_FeutzYvonne(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);

		testfall.createFall(null);
		testfall.createGesuch(null);
		Gesuch gesuch = testfall.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());
		checkTestfall02FeutzYvonne(gesuch);
	}

	@Test
	public void testfall03_PerreiraMarcia() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		Testfall03_PerreiraMarcia testfall = new Testfall03_PerreiraMarcia(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);

		testfall.createFall(null);
		testfall.createGesuch(null);
		Gesuch gesuch = testfall.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());
		checkTestfall03PerreiraMarcia(gesuch);
	}

	@Test
	public void testfall04_WaltherLaura() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		Testfall04_WaltherLaura testfall = new Testfall04_WaltherLaura(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);

		testfall.createFall(null);
		testfall.createGesuch(null);
		Gesuch gesuch = testfall.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());
		checkTestfall04WaltherLaura(gesuch);
	}

	@Test
	public void testfall05_LuethiMeret() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		Testfall05_LuethiMeret testfall = new Testfall05_LuethiMeret(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);

		testfall.createFall(null);
		testfall.createGesuch(null);
		Gesuch gesuch = testfall.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());
		checkTestfall05LuethiMeret(gesuch);
	}

	@Test
	public void testfall06_BeckerNora() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenTagiWeissenstein());
		Testfall06_BeckerNora testfall = new Testfall06_BeckerNora(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);

		testfall.createFall(null);
		testfall.createGesuch(null);
		Gesuch gesuch = testfall.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());
		checkTestfall06BeckerNora(gesuch);
	}
}
