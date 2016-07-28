package ch.dvbern.ebegu.tests.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.testfaelle.AbstractTestfall;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.junit.Assert;
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
		for (KindContainer kindContainer : gesuch.getKindContainers()) {
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				if (betreuung.getInstitutionStammdaten().getInstitution().getId().equals(AbstractTestfall.idInstitutionAaregg)) {
					Verfuegung verfuegung = betreuung.getVerfuegung();
					System.out.println(verfuegung);
					Assert.assertEquals(12, verfuegung.getZeitabschnitte().size());
					// Erster Monat
					VerfuegungZeitabschnitt august = verfuegung.getZeitabschnitte().get(0);
					assertZeitabschnitt(august, 80, 80, 80, 1827.05, 1562.25, 264.80);
					// Letzter Monat
					VerfuegungZeitabschnitt januar = verfuegung.getZeitabschnitte().get(5);
					assertZeitabschnitt(januar, 80, 80, 80, 1827.05, 1562.25, 264.80);
					// Kein Anspruch mehr ab Februar
					VerfuegungZeitabschnitt februar = verfuegung.getZeitabschnitte().get(6);
					assertZeitabschnitt(februar, 0, 80, 0, 0, 0, 0);
				} else {
					Verfuegung verfuegung = betreuung.getVerfuegung();
					System.out.println(verfuegung);
					Assert.assertEquals(12, verfuegung.getZeitabschnitte().size());
					// Noch kein Anspruch bis januar
					VerfuegungZeitabschnitt januar = verfuegung.getZeitabschnitte().get(5);
					assertZeitabschnitt(januar, 0, 80, 0, 0, 0, 0);
					// Erster Monat
					VerfuegungZeitabschnitt februar = verfuegung.getZeitabschnitte().get(6);
					assertZeitabschnitt(februar, 40, 80, 40, 913.50, 781.10, 132.40);
					// Letzter Monat
					VerfuegungZeitabschnitt juli = verfuegung.getZeitabschnitte().get(11);
					assertZeitabschnitt(juli, 40, 80, 40, 913.50, 781.10, 132.40);
				}
			}
		}
	}
}
