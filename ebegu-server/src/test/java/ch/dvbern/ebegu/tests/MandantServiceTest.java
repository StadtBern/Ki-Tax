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

import java.util.Optional;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.services.MandantService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests fuer die Klasse MandantService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class MandantServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private MandantService mandantService;

	@Inject
	private Persistence persistence;




	@Test
	public void findMandantTest() {
		Mandant mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		Optional<Mandant> mandantOpt = mandantService.findMandant(mandant.getId());
		Assert.assertTrue(mandantOpt.isPresent());
		Assert.assertEquals("Mandant1", mandantOpt.get().getName());
	}

	@Test
	public void firstMandantTest() {
		Mandant mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		Mandant loadedMandant = mandantService.getFirst();
		Assert.assertNotNull(loadedMandant);
		Assert.assertEquals("Mandant1", loadedMandant.getName());
	}
}
