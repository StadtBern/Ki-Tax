package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.enums.GesuchBetreuungenStatus;
import ch.dvbern.ebegu.util.AntragStatusConverterUtil;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;

/**
 * Tests der die Konvertierung vom AntragStatus
 */
public class AntragStatusConverterUtilTest {


	@Test
	public void convertStatusToDTOGEPRUEFTTest() {
		Gesuch gesuch = TestDataUtil.createTestgesuchDagmar(); // by default
		AntragStatusDTO antragStatusDTO = AntragStatusConverterUtil.convertStatusToDTO(gesuch, AntragStatus.GEPRUEFT);
		Assert.assertEquals(AntragStatusDTO.GEPRUEFT, antragStatusDTO);
	}

	@Test
	public void convertStatusToDTOAlleBestaetigtTest() {
		final Gesuch gesuch = prepareGesuchAndBetreuungsstatus(GesuchBetreuungenStatus.ALLE_BESTAETIGT);

		AntragStatusDTO antragStatusDTO = AntragStatusConverterUtil.convertStatusToDTO(gesuch, AntragStatus.GEPRUEFT);
		Assert.assertEquals(AntragStatusDTO.GEPRUEFT, antragStatusDTO);
	}

	@Test
	public void convertStatusToDTOEinsAbgewiesenTest() {
		final Gesuch gesuch = prepareGesuchAndBetreuungsstatus(GesuchBetreuungenStatus.ABGEWIESEN);

		AntragStatusDTO antragStatusDTO = AntragStatusConverterUtil.convertStatusToDTO(gesuch, AntragStatus.GEPRUEFT);
		Assert.assertEquals(AntragStatusDTO.PLATZBESTAETIGUNG_ABGEWIESEN, antragStatusDTO);
	}

	@Test
	public void convertStatusToDTOEinsWartenTest() {
		final Gesuch gesuch = prepareGesuchAndBetreuungsstatus(GesuchBetreuungenStatus.WARTEN);

		AntragStatusDTO antragStatusDTO = AntragStatusConverterUtil.convertStatusToDTO(gesuch, AntragStatus.GEPRUEFT);
		Assert.assertEquals(AntragStatusDTO.PLATZBESTAETIGUNG_WARTEN, antragStatusDTO);
	}

	@Test
	public void convertStatusToEntityGEPRUEFTTest() {
		Assert.assertEquals(AntragStatus.GEPRUEFT, AntragStatusConverterUtil.convertStatusToEntity(AntragStatusDTO.PLATZBESTAETIGUNG_WARTEN));
		Assert.assertEquals(AntragStatus.GEPRUEFT, AntragStatusConverterUtil.convertStatusToEntity(AntragStatusDTO.PLATZBESTAETIGUNG_ABGEWIESEN));
		Assert.assertEquals(AntragStatus.GEPRUEFT, AntragStatusConverterUtil.convertStatusToEntity(AntragStatusDTO.GEPRUEFT));
	}


	// HELP METHODS

	@Nonnull
	private Gesuch prepareGesuchAndBetreuungsstatus(GesuchBetreuungenStatus status) {
		final Gesuch gesuch = TestDataUtil.createTestgesuchDagmar();
		gesuch.setGesuchBetreuungenStatus(status);
		return gesuch;
	}
}
