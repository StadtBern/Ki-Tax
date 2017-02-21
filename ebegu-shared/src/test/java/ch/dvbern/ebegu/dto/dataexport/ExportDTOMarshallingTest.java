package ch.dvbern.ebegu.dto.dataexport;

import ch.dvbern.ebegu.dto.dataexport.v1.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Land;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Testing Marshalling
 */
public class ExportDTOMarshallingTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testMarshalling() throws JAXBException, IOException {

		VerfuegungenExportDTO exportDTO = createTestdata();

		// create JAXB context and instantiate marshaller
		JAXBContext context = JAXBContext.newInstance(VerfuegungenExportDTO.class);
		Marshaller marshaller = context.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		// Write to System.out
//		marshaller.marshal(exportDTO, System.out);

		File createdFile = folder.newFile("myTestExportDto.xml");
		marshaller.marshal(exportDTO, createdFile);
		// get variables from our xml file, created before

		Unmarshaller um = context.createUnmarshaller();
		VerfuegungenExportDTO readBeackVerfuegung = (VerfuegungenExportDTO) um.unmarshal(new FileReader(createdFile));
		List<VerfuegungExportDTO> verfuegungen = readBeackVerfuegung.getVerfuegungen();

		for (VerfuegungExportDTO dto : verfuegungen) {
			Assert.assertEquals("16.0000001.1.1", dto.getRefnr());
		}
		Assert.assertEquals(exportDTO, readBeackVerfuegung);
	}


	@Test
	public void testUnmarshallingPredefined() throws JAXBException, IOException {

		JAXBContext context = JAXBContext.newInstance(VerfuegungenExportDTO.class);
		Marshaller marshaller = context.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);


		InputStream fileToRead = this.getClass().getResourceAsStream("exportVerfuegungenTest.xml");
		// get variables from our xml file, created before

		Unmarshaller um = context.createUnmarshaller();
		VerfuegungenExportDTO readBeackVerfuegung = (VerfuegungenExportDTO) um.unmarshal(fileToRead);
		Assert.assertNotNull(readBeackVerfuegung);
		List<VerfuegungExportDTO> verfuegungen = readBeackVerfuegung.getVerfuegungen();

		for (VerfuegungExportDTO dto : verfuegungen) {
			Assert.assertEquals("16.0000001.1.1", dto.getRefnr());
		}
		VerfuegungenExportDTO exportDTO = createTestdata();

		Assert.assertEquals(exportDTO, readBeackVerfuegung);
	}


	@Nonnull
	private VerfuegungenExportDTO createTestdata() {
		VerfuegungenExportDTO exportDTO = new VerfuegungenExportDTO();


		VerfuegungExportDTO verfuegungExportDTO = new VerfuegungExportDTO();
		List<VerfuegungExportDTO> listToExport = new ArrayList<>();
		listToExport.add(verfuegungExportDTO);
		exportDTO.setVerfuegungen(listToExport);

		verfuegungExportDTO.setRefnr("16.0000001.1.1");
		verfuegungExportDTO.setVon(LocalDate.of(2016, 8, 1));
		verfuegungExportDTO.setVon(LocalDate.of(2017, 7, 31));
		verfuegungExportDTO.setVersion(1);
		verfuegungExportDTO.setVerfuegtAm(LocalDateTime.of(2016, 6, 1, 0, 0));
		verfuegungExportDTO.setKind(new KindExportDTO("Henk", "Honolulu", LocalDate.of(2016, 6, 12)));
		verfuegungExportDTO.setGesuchsteller(new GesuchstellerExportDTO("George", "Honolulu", "somebody@somewhere.org"));

		BetreuungExportDTO betreuungExportDTO = new BetreuungExportDTO();
		betreuungExportDTO.setBetreuungsArt(BetreuungsangebotTyp.KITA);
		AdresseExportDTO adr = new AdresseExportDTO("Nussbaumstrasse", "21", null, "Bern", "3006", Land.CH);
		InstitutionExportDTO inst = new InstitutionExportDTO("545b8d2d-da72-4232-b562-0ff64706feea", "Brürnnen", "LeoLea", adr);

		betreuungExportDTO.setInstitution(inst);
		verfuegungExportDTO.setBetreuung(betreuungExportDTO);
		List<ZeitabschnittExportDTO> listOfAbschn = new ArrayList<>();

		ZeitabschnittExportDTO firstAbschnitt = createZeitabschnitt();
		listOfAbschn.add(firstAbschnitt);

		verfuegungExportDTO.setZeitabschnitte(listOfAbschn);
		return exportDTO;
	}

	@Nonnull
	private ZeitabschnittExportDTO createZeitabschnitt() {
		LocalDate von = LocalDate.of(2017, 1, 1);
		LocalDate bis = LocalDate.of(2017, 1, 31);

		int effBet = 80;
		int anspPct = 60;
		int vergPct = 60;
		BigDecimal vollkosten = new BigDecimal("1370.05");
		BigDecimal verg = new BigDecimal("690.45");
		return new ZeitabschnittExportDTO(von, bis, effBet, anspPct, vergPct, vollkosten, verg);
	}

}
