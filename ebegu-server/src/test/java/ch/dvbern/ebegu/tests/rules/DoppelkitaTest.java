package ch.dvbern.ebegu.tests.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.testfaelle.AbstractTestfall;
import ch.dvbern.ebegu.testfaelle.Testfall_DoppelKita_Regel1;
import ch.dvbern.ebegu.testfaelle.Testfall_DoppelKita_Regel2;
import ch.dvbern.ebegu.testfaelle.Testfall_DoppelKita_Regel3;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Test der die vom JA gemeldeten Testfaelle für Doppelkitas ueberprueft.
 * Siehe https://support.dvbern.ch/browse/EBEGU-561
 */
public class DoppelkitaTest extends AbstractBGRechnerTest {


	/**
	 * Testet Regel 1: Bei gleichzeitigem Beginn gewinnt die Kita mit dem höheren Pensum
	 */
	@Test
	public void testfall_Doppelkita_01() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		Testfall_DoppelKita_Regel1 testfall = new Testfall_DoppelKita_Regel1(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);

		testfall.createFall(null);
		testfall.createGesuch(LocalDate.of(2016, 7, 1));
		Gesuch gesuch = testfall.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());

		for (KindContainer kindContainer : gesuch.getKindContainers()) {
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				if (betreuung.getInstitutionStammdaten().getInstitution().getId().equals(AbstractTestfall.ID_INSTITUTION_WEISSENSTEIN)) {
					Verfuegung verfuegung = betreuung.getVerfuegung();
					System.out.println(verfuegung);
					Assert.assertEquals(12, verfuegung.getZeitabschnitte().size());
					// August
					VerfuegungZeitabschnitt august = verfuegung.getZeitabschnitte().get(0);
					assertZeitabschnitt(august, 40, 10, 10);
					// Januar
					VerfuegungZeitabschnitt januar = verfuegung.getZeitabschnitte().get(5);
					assertZeitabschnitt(januar, 50, 20, 20);
				} else {     //KITA Bruennen
					Verfuegung verfuegung = betreuung.getVerfuegung();
					System.out.println(verfuegung);
					Assert.assertEquals(12, verfuegung.getZeitabschnitte().size());
					// August
					VerfuegungZeitabschnitt august = verfuegung.getZeitabschnitte().get(0);
					assertZeitabschnitt(august, 50, 60, 50);
					// Januar
					VerfuegungZeitabschnitt januar = verfuegung.getZeitabschnitte().get(5);
					assertZeitabschnitt(januar, 40, 60, 40);
				}
			}
		}
	}

	/**
	 * Testet Regel 2: Die Kita, deren Betreuung früher beginnt, gewinnt unabhängig von der Höhe des Pensums
	 */
	@Test
	public void testfall_Doppelkita_02() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		Testfall_DoppelKita_Regel2 testfall = new Testfall_DoppelKita_Regel2(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);

		testfall.createFall(null);
		testfall.createGesuch(LocalDate.of(2016, 7, 1));
		Gesuch gesuch = testfall.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());

		for (KindContainer kindContainer : gesuch.getKindContainers()) {
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				if (betreuung.getInstitutionStammdaten().getInstitution().getId().equals(AbstractTestfall.ID_INSTITUTION_WEISSENSTEIN)) {
					Verfuegung verfuegung = betreuung.getVerfuegung();
					System.out.println(verfuegung);
					Assert.assertEquals(12, verfuegung.getZeitabschnitte().size());
					// Erster Monat
					VerfuegungZeitabschnitt august = verfuegung.getZeitabschnitte().get(0);
					assertZeitabschnitt(august, 40, 60, 40);
					// Januar
					VerfuegungZeitabschnitt januar = verfuegung.getZeitabschnitte().get(5);
					assertZeitabschnitt(januar, 50, 60, 50);
				} else {     //KITA Bruennen
					Verfuegung verfuegung = betreuung.getVerfuegung();
					System.out.println(verfuegung);
					Assert.assertEquals(12, verfuegung.getZeitabschnitte().size());
					// Erster Monat
					VerfuegungZeitabschnitt okbober = verfuegung.getZeitabschnitte().get(2); // Oktober
					assertZeitabschnitt(okbober, 50, 20, 20);
					// Januar
					VerfuegungZeitabschnitt januar = verfuegung.getZeitabschnitte().get(5);
					assertZeitabschnitt(januar, 40, 10, 10);
				}
			}
		}
	}

	/**
	 * Bei gleichzeitigem Beginn und gleichem Pensum wird die erst eingegebene Kita zuerst bedient
	 */
	@Test
	public void testfall_Doppelkita_03() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		Testfall_DoppelKita_Regel3 testfall = new Testfall_DoppelKita_Regel3(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);

		testfall.createFall(null);
		testfall.createGesuch(LocalDate.of(2016, 7, 1));
		Gesuch gesuch = testfall.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, getParameter());

		for (KindContainer kindContainer : gesuch.getKindContainers()) {
			for (Betreuung betreuung : kindContainer.getBetreuungen()) {
				if (betreuung.getInstitutionStammdaten().getInstitution().getId().equals(AbstractTestfall.ID_INSTITUTION_WEISSENSTEIN)) {
					Verfuegung verfuegung = betreuung.getVerfuegung();
					System.out.println(verfuegung);
					Assert.assertEquals(12, verfuegung.getZeitabschnitte().size());
					// Erster Monat
					VerfuegungZeitabschnitt august = verfuegung.getZeitabschnitte().get(0);
					assertZeitabschnitt(august, 40, 60, 40);
					// Januar
					VerfuegungZeitabschnitt januar = verfuegung.getZeitabschnitte().get(5);
					assertZeitabschnitt(januar, 30, 60, 30);
				} else {     //KITA Bruennen
					Verfuegung verfuegung = betreuung.getVerfuegung();
					System.out.println(verfuegung);
					Assert.assertEquals(12, verfuegung.getZeitabschnitte().size());
					// Erster Monat
					VerfuegungZeitabschnitt august = verfuegung.getZeitabschnitte().get(0);
					assertZeitabschnitt(august, 40, 20, 20);
					// Januar
					VerfuegungZeitabschnitt januar = verfuegung.getZeitabschnitte().get(5);
					assertZeitabschnitt(januar, 50, 30, 30);
				}
			}
		}
	}
}
