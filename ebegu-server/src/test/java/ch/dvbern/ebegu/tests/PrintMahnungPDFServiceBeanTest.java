package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.enums.MahnungTyp;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.services.EbeguVorlageService;
import ch.dvbern.ebegu.services.PrintMahnungPDFServiceBean;
import ch.dvbern.ebegu.testfaelle.Testfall02_FeutzYvonne;
import ch.dvbern.ebegu.tets.TestDataUtil;
import de.akquinet.jbosscc.needle.annotation.InjectIntoMany;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class PrintMahnungPDFServiceBeanTest {

	protected BetreuungsgutscheinEvaluator evaluator;

	@Rule
	public NeedleRule needleRule = new NeedleRule();

	@ObjectUnderTest
	private PrintMahnungPDFServiceBean printMahnungPDFService;

	@InjectIntoMany
	EbeguVorlageService vorlageService = new EbeguVorlageServiceMock();

	@Before
	public void setUpCalcuator() {

		evaluator = AbstractBGRechnerTest.createEvaluator();
	}

	@Test
	public void testPrintErsteMahnung() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenTagiAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());

		Testfall02_FeutzYvonne testfall = new Testfall02_FeutzYvonne(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		testfall.createFall(null);
		testfall.createGesuch(LocalDate.of(2016, Month.DECEMBER, 12));

		Gesuch gesuch = testfall.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());

		Mahnung mahnung = TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch);

		byte[] bytes = printMahnungPDFService.printMahnung(mahnung, null);

		writeToTempDir(bytes, "1_Mahnung.pdf");
	}

	@Test
	public void testPrintZweiteMahnung() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenTagiAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());

		Testfall02_FeutzYvonne testfall = new Testfall02_FeutzYvonne(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		testfall.createFall(null);
		testfall.createGesuch(LocalDate.of(2016, Month.DECEMBER, 12));

		Gesuch gesuch = testfall.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());

		Mahnung ersteMahnung = TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch);
		Mahnung zweiteMahnung = TestDataUtil.createMahnung(MahnungTyp.ZWEITE_MAHNUNG, gesuch);
		zweiteMahnung.setVorgaengerId(ersteMahnung.getId());

		byte[] bytes = printMahnungPDFService.printMahnung(zweiteMahnung, Optional.of(ersteMahnung));

		writeToTempDir(bytes, "2_Mahnung.pdf");
	}

	private final File writeToTempDir(final byte[] data, final String fileName) throws IOException {

		File tempFile = null;

		FileOutputStream fos = null;
		try {
			// create temp file in junit temp folder
			//tempFile = tempFolder.newFile(fileName);
			tempFile = new File("C:/Development/EBEGU/JUnitTestTemp", fileName);
			System.out.println("Writing tempfile to: " + tempFile);
			fos = new FileOutputStream(tempFile);
			fos.write(data);
			fos.close();
			// File external oeffnen
			//openPDF(tempFile);
		} finally {
			if (fos != null) {
				fos.close();
			}

		}
		return tempFile;
	}
}
