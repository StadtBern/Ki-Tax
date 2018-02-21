
/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.TestdataCreationService;
import ch.dvbern.ebegu.util.TestfallName;
import ch.dvbern.ebegu.util.testdata.AnmeldungConfig;
import ch.dvbern.ebegu.util.testdata.ErstgesuchConfig;
import ch.dvbern.ebegu.util.testdata.MutationConfig;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Arquillian Tests fuer die Klasse GesuchService
 */
@SuppressWarnings("LocalVariableNamingConvention")
@RunWith(Arquillian.class)
@Transactional(TransactionMode.DISABLED)
public class GesuchServiceBeanTest extends AbstractTestdataCreationTest {

	@Inject
	private FallService fallService;

	@Inject
	private TestdataCreationService testdataCreationService;


	@Test
	public void deleteGesuchWithMutationAndCopiedAnmeldungen() {
		ErstgesuchConfig config = ErstgesuchConfig.createErstgesuchVerfuegt(TestfallName.LUETHI_MERET, LocalDate.now(), LocalDateTime.now());
		Gesuch erstgesuch = testdataCreationService.createErstgesuch(config);
		erstgesuch = testdataCreationService.addAnmeldung(AnmeldungConfig.createAnmeldungTagesschule(), erstgesuch);

		Gesuch mutation = testdataCreationService.createMutation(MutationConfig.createEmptyMutationVerfuegt(LocalDate.now(), LocalDateTime.now()), erstgesuch);
		testdataCreationService.addAnmeldung(AnmeldungConfig.createAnmeldungTagesschule(), mutation);

		fallService.removeFall(erstgesuch.getFall());
		Assert.assertTrue("Gesuche wurden in der richtigen Reihenfolge geloscht", fallService.getAllFalle(false).isEmpty());
	}
}
