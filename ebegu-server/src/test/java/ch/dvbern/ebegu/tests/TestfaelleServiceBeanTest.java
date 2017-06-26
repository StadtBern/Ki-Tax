package ch.dvbern.ebegu.tests;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.services.EbeguParameterService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;
import ch.dvbern.ebegu.services.TestfaelleService;
import ch.dvbern.ebegu.services.TraegerschaftService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.tets.data.VerfuegungZeitabschnittData;
import ch.dvbern.ebegu.tets.data.VerfuegungszeitabschnitteData;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration Test mit den Testfällen.
 * Alle Verfügungsdaten - Ergebnisse werden in den .xml Files unter /src/test/resources/VerfuegungResult/ gespeichert
 * und bei jedem Test mit den aktuellen Berechnungsergebnisse verglichen.
 * <p>
 * Die gespeicherten Daten können mit writeToFile = true neu generiert werden.
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class TestfaelleServiceBeanTest extends AbstractEbeguLoginTest {

	private static final Logger LOG = LoggerFactory.getLogger(TestfaelleServiceBeanTest.class);

	@Inject
	private TestfaelleService testfaelleService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private InstitutionStammdatenService institutionStammdatenService;

	@Inject
	private TraegerschaftService traegerschaftService;

	@Inject
	private Persistence<?> persistence;

	@Inject
	private EbeguParameterService ebeguParameterService;

	@Inject
	private GesuchService gesuchService;


	/**
	 * Wenn true werden die Testergebnisse neu in die Testfiles geschrieben. Muss für testen immer false sein!
	 */
	private static final boolean writeToFile = false;

	@Before
	public void init() {
		final Gesuchsperiode gesuchsperiode = createGesuchsperiode(true);
		final Mandant mandant = insertInstitutionen();
		createBenutzer(mandant);
		TestDataUtil.prepareParameters(gesuchsperiode.getGueltigkeit(), persistence);
	}

	@Test
	public void testVerfuegung_WaeltiDagmar() {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.WAELTI_DAGMAR, true, true);
		ueberpruefeVerfuegungszeitabschnitte(gesuch, null);
	}

	@Test
	public void testVerfuegung_FeutzIvonne() {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.FEUTZ_IVONNE, true, true);
		ueberpruefeVerfuegungszeitabschnitte(gesuch, null);
	}

	@Test
	public void testVerfuegung_BeckerNora() {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.BECKER_NORA, true, true);
		ueberpruefeVerfuegungszeitabschnitte(gesuch, null);
	}

	@Test
	public void testVerfuegung_LuethiMeret() {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.LUETHI_MERET, true, true);
		ueberpruefeVerfuegungszeitabschnitte(gesuch, null);
	}

	@Test
	public void testVerfuegung_PerreiraMarcia() {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.PERREIRA_MARCIA, true, true);
		ueberpruefeVerfuegungszeitabschnitte(gesuch, null);
	}

	@Test
	public void testVerfuegung_WaltherLaura() {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.WALTHER_LAURA, true, true);
		ueberpruefeVerfuegungszeitabschnitte(gesuch, null);
	}

	@Test
	public void testVerfuegung_MeierMeret() {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.MEIER_MERET, true, true);
		ueberpruefeVerfuegungszeitabschnitte(gesuch, null);
	}

	@Test
	public void testVerfuegung_UmzugAusInAusBern() {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.UMZUG_AUS_IN_AUS_BERN, true, true);
		ueberpruefeVerfuegungszeitabschnitte(gesuch, null);
	}

	@Test
	public void testVerfuegung_UmzugVorGesuchsperiode() {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.UMZUG_VOR_GESUCHSPERIODE, true, true);
		ueberpruefeVerfuegungszeitabschnitte(gesuch, null);
	}

	@Test
	public void testVerfuegung_Abwesenheit() {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.ABWESENHEIT, true, true);
		ueberpruefeVerfuegungszeitabschnitte(gesuch, null);
	}

	@Test
	public void testVerfuegung_WaeltiDagmar_mutationHeirat() {
		//waelti dagmar arbeitet 60% und hat 20% zuschlag zum ewp
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.WAELTI_DAGMAR, true, true);
		assert gesuch != null;
		final Gesuch mutieren = testfaelleService.mutierenHeirat(gesuch.getFall().getFallNummer(),
			gesuch.getGesuchsperiode().getId(), LocalDate.of(2016, Month.DECEMBER, 15), LocalDate.of(2017, Month.JANUARY, 15), true);
		ueberpruefeVerfuegungszeitabschnitte(mutieren, "MutationHeirat");
	}

	@Test
	public void testVerfuegung_BeckerNora_mutationHeirat() {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.BECKER_NORA, true, true);
		assert gesuch != null;
		final Gesuch mutieren = testfaelleService.mutierenHeirat(gesuch.getFall().getFallNummer(),
			gesuch.getGesuchsperiode().getId(), LocalDate.of(2017, Month.FEBRUARY, 15), LocalDate.of(2017, Month.FEBRUARY, 15), true);
		ueberpruefeVerfuegungszeitabschnitte(mutieren, "MutationHeirat");
	}

	@Test
	public void testVerfuegung_PerreiraMarcia_mutationScheidung() {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.PERREIRA_MARCIA, true, true);
		assert gesuch != null;
		final Gesuch mutieren = testfaelleService.mutierenScheidung(gesuch.getFall().getFallNummer(),
			gesuch.getGesuchsperiode().getId(), LocalDate.of(2016, Month.SEPTEMBER, 30), LocalDate.of(2016, Month.OCTOBER, 15), true);
		ueberpruefeVerfuegungszeitabschnitte(mutieren, "MutationScheidung");
	}

	/**
	 * Diese Scheidung wurde zu spät eingereicht!
	 */
	@Test
	public void testVerfuegung_MeierMeret_mutationScheidung() {
		Gesuch gesuch = testfaelleService.createAndSaveTestfaelle(TestfaelleService.MEIER_MERET, true, true);
		assert gesuch != null;
		final Gesuch mutieren = testfaelleService.mutierenScheidung(gesuch.getFall().getFallNummer(),
			gesuch.getGesuchsperiode().getId(), LocalDate.of(2016, Month.NOVEMBER, 15), LocalDate.of(2016, Month.OCTOBER, 15), true);
		ueberpruefeVerfuegungszeitabschnitte(mutieren, "MutationScheidung");
	}


	/**
	 * Ueberprüfen der Verfügungszeitabschnitte
	 *
	 * @param gesuch
	 * @param addText
	 */
	private void ueberpruefeVerfuegungszeitabschnitte(Gesuch gesuch, String addText) {
		Assert.assertNotNull(gesuch);

		gesuch.getKindContainers().stream().forEach(kindContainer -> {
				kindContainer.getBetreuungen().stream().forEach(betreuung -> {
					writeResultsToFile(betreuung.getVerfuegung().getZeitabschnitte(), kindContainer.getKindJA().getFullName(),
						betreuung.getInstitutionStammdaten().getInstitution().getName(), betreuung.getBetreuungNummer(), addText);
					compareWithDataInFile(betreuung.getVerfuegung().getZeitabschnitte(),
						kindContainer.getKindJA().getFullName(), betreuung.getInstitutionStammdaten().getInstitution().getName(), betreuung.getBetreuungNummer(), addText);
				});
			}
		);
	}

	/**
	 * Holt die gespeicherten Verfügungszeitabschnitte und vergleicht diese mit den berechneten
	 */
	private void compareWithDataInFile(List<VerfuegungZeitabschnitt> zeitabschnitte, String fullName, String betreuung, Integer betreuungNummer, String addText) {
		final VerfuegungszeitabschnitteData expectedVerfuegungszeitabschnitt = getExpectedVerfuegungszeitabschnitt(fullName, betreuung, betreuungNummer, addText);
		final VerfuegungszeitabschnitteData calculatedVerfuegungszeitabschnitt = generateVzd(zeitabschnitte, fullName, betreuung, betreuungNummer);

		final Iterator<VerfuegungZeitabschnittData> iteratorExpected = expectedVerfuegungszeitabschnitt.getVerfuegungszeitabschnitte().iterator();
		final Iterator<VerfuegungZeitabschnittData> iteratorCalculated = calculatedVerfuegungszeitabschnitt.getVerfuegungszeitabschnitte().iterator();
		while (iteratorExpected.hasNext() &&
			iteratorCalculated.hasNext()) {
			doCompare(iteratorExpected.next(),
				iteratorCalculated.next(), fullName, betreuung);
		}
	}

	/**
	 * Vergleicht die einzelnen Werte der Verfuegungszeitabschnitte
	 */
	private void doCompare(VerfuegungZeitabschnittData expected, VerfuegungZeitabschnittData calculated, String fullName, String betreuung) {
		String fehlerString = "Unterschiedliches Resultat beim gespeicherten und berechnetem Wert " +
			"fuer Kind " + fullName + " bei Betreuung " + betreuung + " bei(m): ";

		Assert.assertEquals(fehlerString + "GueltigAb", expected.getGueltigAb(), calculated.getGueltigAb());
		Assert.assertEquals(fehlerString + "Gueltig Bis", expected.getGueltigBis(), calculated.getGueltigBis());
		Assert.assertEquals(fehlerString + "Abzug der Familiengroesse", expected.getAbzugFamGroesse(), calculated.getAbzugFamGroesse());
		Assert.assertEquals(fehlerString + "Elternbeitrag", expected.getElternbeitrag(), calculated.getElternbeitrag());
		Assert.assertEquals(fehlerString + "Anspruchberechtigtes Pensum", expected.getAnspruchberechtigtesPensum(), calculated.getAnspruchberechtigtesPensum());
		Assert.assertEquals(fehlerString + "Bemerkungen", expected.getBemerkungen(), calculated.getBemerkungen());
		Assert.assertEquals(fehlerString + "Betreuungspensum", expected.getBetreuungspensum(), calculated.getBetreuungspensum());
		Assert.assertEquals(fehlerString + "Vollkosten", expected.getVollkosten(), calculated.getVollkosten());
	}

	/**
	 * Holt die gespeicherten Werte aus den Files
	 */
	public VerfuegungszeitabschnitteData getExpectedVerfuegungszeitabschnitt(String fullName, String betreuung, Integer betreuungNummer, String addText) {

		final String fileNamePath = getFileNamePath(fullName, betreuung, betreuungNummer, addText);
		final File resultFile = new File(fileNamePath);
		VerfuegungszeitabschnitteData expectedVerfuegungszeitabschnitteData = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(VerfuegungszeitabschnitteData.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			expectedVerfuegungszeitabschnitteData = (VerfuegungszeitabschnitteData) jaxbUnmarshaller.unmarshal(resultFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return expectedVerfuegungszeitabschnitteData;
	}

	/**
	 * Schreibt die berechneten Werte in die Files wenn writeToFile true ist
	 */
	public void writeResultsToFile(final List<VerfuegungZeitabschnitt> verfuegungZeitabschnitts, String fullName, String betreuung, Integer betreuungNummer, String addText) {
		if (writeToFile) {
			VerfuegungszeitabschnitteData eventResults = generateVzd(verfuegungZeitabschnitts, fullName, betreuung, betreuungNummer);

			String pathname = getFileNamePath(fullName, betreuung, betreuungNummer, addText);

			try {
				File file = new File(pathname);
				JAXBContext jaxbContext = JAXBContext.newInstance(VerfuegungszeitabschnitteData.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

				// output pretty printed
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

				jaxbMarshaller.marshal(eventResults, file);

			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Generiert den Pfad für die Files zum speichern der Daten
	 */
	private String getFileNamePath(String fullName, String betreuung, Integer betreungsnummer, String addText) {
		String storePath = "./src/test/resources/VerfuegungResult/";

		String filename = fullName + betreuung + betreungsnummer;
		if (addText != null) {
			filename += addText;
		}
		return storePath + filename.replaceAll("[^a-zA-Z0-9.-]", "_") + ".xml";
	}

	/**
	 * Schreibt die berechneten Daten in VerfuegungszeitabschnitteData objekt
	 */
	public VerfuegungszeitabschnitteData generateVzd(final List<VerfuegungZeitabschnitt> verfuegungZeitabschnitts, String fullName, String betreuung, Integer betreuungNummer) {
		VerfuegungszeitabschnitteData verfuegungszeitabschnitteData = new VerfuegungszeitabschnitteData();

		verfuegungszeitabschnitteData.setNameBetreung(betreuung);
		verfuegungszeitabschnitteData.setNameKind(fullName);
		verfuegungszeitabschnitteData.setNummer(betreuungNummer);

		for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : verfuegungZeitabschnitts) {
			VerfuegungZeitabschnittData verfuegungZeitabschnittData = new VerfuegungZeitabschnittData(verfuegungZeitabschnitt);
			verfuegungszeitabschnitteData.getVerfuegungszeitabschnitte().add(verfuegungZeitabschnittData);
		}

		return verfuegungszeitabschnitteData;
	}

	/**
	 * Helper für init. Speichert Gesuchsperiode in DB
	 */
	protected Gesuchsperiode createGesuchsperiode(boolean active) {
		Gesuchsperiode gesuchsperiode = TestDataUtil.createCustomGesuchsperiode(2016, 2017);
		gesuchsperiode.setStatus(GesuchsperiodeStatus.AKTIV);
		gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode);
		return gesuchsperiode;
	}
}

