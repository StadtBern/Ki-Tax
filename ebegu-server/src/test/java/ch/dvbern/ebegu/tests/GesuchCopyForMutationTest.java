package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.Eingangsart;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Test copy for mutation
 */
public class GesuchCopyForMutationTest {
	@Test
	public void copyForMutation() throws Exception {

		Collection<InstitutionStammdaten> instStammdaten = new ArrayList<>();

		Gesuchsperiode gesuchsperiode = TestDataUtil.createGesuchsperiode1718();
		Testfall01_WaeltiDagmar testfall01_waeltiDagmar =
			new Testfall01_WaeltiDagmar(gesuchsperiode, instStammdaten);


		testfall01_waeltiDagmar.createGesuch(LocalDate.now());
		Gesuch gesuch = testfall01_waeltiDagmar.getGesuch();
		Gesuch mutation = gesuch.copyForMutation(new Gesuch(), Eingangsart.PAPIER);
		Assert.assertEquals(Eingangsart.PAPIER, mutation.getEingangsart());
		Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_JA, mutation.getStatus());

		Gesuch mutation2 = gesuch.copyForMutation(new Gesuch(), Eingangsart.ONLINE);
		Assert.assertEquals(Eingangsart.ONLINE, mutation2.getEingangsart());
		Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_GS, mutation2.getStatus());

		Assert.assertEquals(AntragTyp.MUTATION, mutation2.getTyp());
		Assert.assertNull(mutation2.getEingangsdatum());

	}

}
