package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.AntragStatusConverter;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * Tests der die Konvertierung vom AntragStatus
 */
public class AntragStatusConverterTest {

	private AntragStatusConverter converter = new AntragStatusConverter();

	@Test
	public void convertStatusToDTOGEPRUEFTTest() {
		Gesuch gesuch = TestDataUtil.createTestgesuchDagmar(); // WARTEN
		AntragStatusDTO antragStatusDTO = converter.convertStatusToDTO(gesuch, AntragStatus.GEPRUEFT);
		Assert.assertEquals(AntragStatusDTO.PLATZBESTAETIGUNG_WARTEN, antragStatusDTO);
	}

	@Test
	public void convertStatusToDTOAlleBestaetigtTest() {
		final Gesuch gesuch = prepareGesuchAndBetreuungsstatus(Betreuungsstatus.BESTAETIGT, Betreuungsstatus.BESTAETIGT);

		AntragStatusDTO antragStatusDTO = converter.convertStatusToDTO(gesuch, AntragStatus.GEPRUEFT);
		Assert.assertEquals(AntragStatusDTO.GEPRUEFT, antragStatusDTO);
	}

	@Test
	public void convertStatusToDTOEinsAbgewiesenTest() {
		final Gesuch gesuch = prepareGesuchAndBetreuungsstatus(Betreuungsstatus.ABGEWIESEN, Betreuungsstatus.BESTAETIGT);

		AntragStatusDTO antragStatusDTO = converter.convertStatusToDTO(gesuch, AntragStatus.GEPRUEFT);
		Assert.assertEquals(AntragStatusDTO.PLATZBESTAETIGUNG_ABGEWIESEN, antragStatusDTO);
	}

	@Test
	public void convertStatusToDTOEinsAbgewiesenEinsWartenTest() {
		final Gesuch gesuch = prepareGesuchAndBetreuungsstatus(Betreuungsstatus.ABGEWIESEN, Betreuungsstatus.WARTEN);

		AntragStatusDTO antragStatusDTO = converter.convertStatusToDTO(gesuch, AntragStatus.GEPRUEFT);
		Assert.assertEquals(AntragStatusDTO.PLATZBESTAETIGUNG_ABGEWIESEN, antragStatusDTO);
	}

	@Test
	public void convertStatusToDTOEinsWartenTest() {
		final Gesuch gesuch = prepareGesuchAndBetreuungsstatus(Betreuungsstatus.BESTAETIGT, Betreuungsstatus.WARTEN);

		AntragStatusDTO antragStatusDTO = converter.convertStatusToDTO(gesuch, AntragStatus.GEPRUEFT);
		Assert.assertEquals(AntragStatusDTO.PLATZBESTAETIGUNG_WARTEN, antragStatusDTO);
	}

	@Test
	public void convertStatusToEntityGEPRUEFTTest() {
		Assert.assertEquals(AntragStatus.GEPRUEFT, converter.convertStatusToEntity(AntragStatusDTO.PLATZBESTAETIGUNG_WARTEN));
		Assert.assertEquals(AntragStatus.GEPRUEFT, converter.convertStatusToEntity(AntragStatusDTO.PLATZBESTAETIGUNG_ABGEWIESEN));
		Assert.assertEquals(AntragStatus.GEPRUEFT, converter.convertStatusToEntity(AntragStatusDTO.GEPRUEFT));
	}


	// HELP METHODS

	@Nonnull
	private Gesuch prepareGesuchAndBetreuungsstatus(Betreuungsstatus status1, Betreuungsstatus status2) {
		final Gesuch gesuch = TestDataUtil.createTestgesuchDagmar();
		final KindContainer kind = gesuch.getKindContainers().iterator().next();
		final Iterator<Betreuung> betreuungIterator = kind.getBetreuungen().iterator();
		betreuungIterator.next().setBetreuungsstatus(status1);
		betreuungIterator.next().setBetreuungsstatus(status2);
		return gesuch;
	}
}
