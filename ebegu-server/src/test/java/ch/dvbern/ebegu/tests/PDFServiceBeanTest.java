package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.services.DokumentGrundService;
import ch.dvbern.ebegu.services.EbeguVorlageService;
import ch.dvbern.ebegu.services.PDFServiceBean;
import ch.dvbern.ebegu.testfaelle.Testfall02_FeutzYvonne;
import ch.dvbern.ebegu.tests.util.UnitTestTempFolder;
import ch.dvbern.ebegu.tets.TestDataUtil;
import de.akquinet.jbosscc.needle.annotation.InjectIntoMany;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 16/11/2016.
 */
@SuppressWarnings("unused")
public class PDFServiceBeanTest {

	@Rule
	public UnitTestTempFolder unitTestTempfolder = new UnitTestTempFolder();

	@Rule
	public NeedleRule needleRule = new NeedleRule();

	@ObjectUnderTest
	private PDFServiceBean pdfService;

	@InjectIntoMany
	EbeguVorlageService vorlageService = new EbeguVorlageServiceMock();

	@InjectIntoMany
	DokumentGrundService dokumentGrundService = new DokumentGrundServiceMock();

	private Gesuch gesuch;

	@Before
	public void setupTestData() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenTagiWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());

		Testfall02_FeutzYvonne testfall = new Testfall02_FeutzYvonne(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		testfall.createFall(TestDataUtil.createDefaultBenutzer());
		testfall.createGesuch(LocalDate.of(2016, Month.DECEMBER, 12));

		gesuch = testfall.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());

		gesuch.addDokumentGrund(new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.STEUERERKLAERUNG));
		gesuch.addDokumentGrund(new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.NACHWEIS_AUSBILDUNG));
		gesuch.addDokumentGrund(new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.NACHWEIS_FAMILIENZULAGEN));
	}

	@Test
	public void	testGenerateFreigabequittungJugendamt() throws Exception {

		byte[] bytes = pdfService.generateFreigabequittung(gesuch, Zustelladresse.JUGENDAMT);
		Assert.assertNotNull(bytes);
		unitTestTempfolder.writeToTempDir(bytes, "Freigabequittung_Jugendamt(" + gesuch.getAntragNummer() + ").pdf");

	}

	@Test
	public void	testGenerateFreigabequittungSchulamt() throws Exception {

		byte[] bytes = pdfService.generateFreigabequittung(gesuch, Zustelladresse.SCHULAMT);
		Assert.assertNotNull(bytes);
		unitTestTempfolder.writeToTempDir(bytes, "Freigabequittung_Schulamt(" + gesuch.getAntragNummer() + ").pdf");

	}

	@Test
	public void testPrintNichteintreten() throws Exception {

		Optional<Betreuung> betreuung = gesuch.extractAllBetreuungen().stream()
			.filter(b -> b.getBetreuungsangebotTyp() == BetreuungsangebotTyp.KITA)
			.findFirst();

		if (betreuung.isPresent()) {
			byte[] bytes = pdfService.generateNichteintreten(betreuung.get());
			Assert.assertNotNull(bytes);
			unitTestTempfolder.writeToTempDir(bytes, "Nichteintreten(" + betreuung.get().getBGNummer() + ").pdf");
		} else {
			throw new Exception(String.format("%s", "testPrintNichteintreten()"));
		}

	}

	@Test
	public void testPrintInfoschreiben() throws Exception {

		Optional<Betreuung> betreuung = gesuch.extractAllBetreuungen().stream()
			.filter(b -> b.getBetreuungsangebotTyp() == BetreuungsangebotTyp.TAGI)
			.findFirst();

		if (betreuung.isPresent()) {
			byte[] bytes = pdfService.generateNichteintreten(betreuung.get());
			Assert.assertNotNull(bytes);
			unitTestTempfolder.writeToTempDir(bytes, "Infoschreiben(" + betreuung.get().getBGNummer() + ").pdf");
		} else {
			throw new Exception(String.format("%s", "testPrintInfoschreiben()"));
		}

	}

	@Test
	public void testPrintErsteMahnung() throws Exception {

		Mahnung mahnung = TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch);

		byte[] bytes = pdfService.generateMahnung(mahnung, null);

		Assert.assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "1_Mahnung.pdf");
	}

	@Test
	public void testPrintZweiteMahnung() throws Exception {

		Mahnung ersteMahnung = TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch);
		Mahnung zweiteMahnung = TestDataUtil.createMahnung(MahnungTyp.ZWEITE_MAHNUNG, gesuch);
		zweiteMahnung.setVorgaengerId(ersteMahnung.getId());

		byte[] bytes = pdfService.generateMahnung(zweiteMahnung, Optional.of(ersteMahnung));
		Assert.assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "2_Mahnung.pdf");
	}

}
