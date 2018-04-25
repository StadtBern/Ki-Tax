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

import javax.inject.Inject;

import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.runner.RunWith;

/**
 * Arquillian Tests fuer die Klasse BenutzerService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class BenutzerServiceBeanTest extends AbstractEbeguLoginTest {

	@Inject
	private BenutzerService benutzerService;

	@Inject
	private Persistence persistence;


//	@Test
//	public void handleAbgelaufeneRollen() {
//		Benutzer benutzer1 = TestDataUtil.createDefaultBenutzer();
//		Benutzer benutzer2 = TestDataUtil.createDefaultBenutzer();
//
//		persistence.merge(benutzer1.getMandant());
//		persistence.merge(benutzer2.getMandant());
//
//		benutzer1.setRole(UserRole.ADMIN);
////		benutzer1.setRoleGueltigBis(LocalDate.now().plusDays(1));
//		benutzerService.saveBenutzer(benutzer1);
//
//		benutzer2.setRole(UserRole.ADMIN);
////		benutzer2.setRoleGueltigBis(LocalDate.now().minusDays(1));
//		benutzerService.saveBenutzer(benutzer2);
//
//		int abgelaufeneRollen = benutzerService.handleAbgelaufeneRollen(LocalDate.now());
//		Assert.assertEquals(1, abgelaufeneRollen);
//
//		abgelaufeneRollen = benutzerService.handleAbgelaufeneRollen(LocalDate.now());
//		Assert.assertEquals(0, abgelaufeneRollen);
//
//		Optional<Benutzer> benutzer1FromDB = benutzerService.findBenutzer(benutzer1.getUsername());
//		Optional<Benutzer> benutzer2FromDB = benutzerService.findBenutzer(benutzer2.getUsername());
//		Assert.assertTrue(benutzer1FromDB.isPresent());
//		Assert.assertTrue(benutzer2FromDB.isPresent());
//		Assert.assertEquals(UserRole.ADMIN, benutzer1FromDB.get().getRole());
//		Assert.assertEquals(LocalDate.now().plusDays(1), benutzer1FromDB.get().getRoleGueltigBis());
//		Assert.assertEquals(UserRole.GESUCHSTELLER, benutzer2FromDB.get().getRole());
//		Assert.assertNull(benutzer2FromDB.get().getRoleGueltigBis());
//	}
}
