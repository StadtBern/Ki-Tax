/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
