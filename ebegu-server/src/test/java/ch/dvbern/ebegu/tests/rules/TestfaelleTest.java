package ch.dvbern.ebegu.tests.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test der die vom JA gemeldeten Testfaelle ueberprueft.
 */
public class TestfaelleTest extends AbstractBGRechnerTest {


	//todo asserts
	@Test
	public void testfall01_WaeltiDagmar() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createDefaultInstitutionStammdaten());
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());
		for (KindContainer kindContainer : gesuch.getKindContainers()) {
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				System.out.println(betreuung.getVerfuegung());
			}
		}
	}
}
