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

import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.services.TraegerschaftService;
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
 * Tests fuer die Klasse TraegerschaftService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class TraegerschaftServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private TraegerschaftService traegerschaftService;

	@Inject
	private Persistence persistence;



	@Test
	public void createTraegerschaft() {
		Assert.assertNotNull(traegerschaftService);
		Traegerschaft traegerschaft = TestDataUtil.createDefaultTraegerschaft();

		traegerschaftService.saveTraegerschaft(traegerschaft);
		Optional<Traegerschaft> traegerschaftOpt = traegerschaftService.findTraegerschaft(traegerschaft.getId());
		Assert.assertTrue(traegerschaftOpt.isPresent());
		Assert.assertEquals("Traegerschaft1", traegerschaftOpt.get().getName());
	}

	@Test
	public void removeTraegerschaft() {
		Assert.assertNotNull(traegerschaftService);
		Traegerschaft traegerschaft = TestDataUtil.createDefaultTraegerschaft();

		traegerschaftService.saveTraegerschaft(traegerschaft);
		Collection<Traegerschaft> allTraegerschaften = traegerschaftService.getAllTraegerschaften();
		Assert.assertEquals(1, allTraegerschaften.size());
		traegerschaftService.removeTraegerschaft(allTraegerschaften.iterator().next().getId());
		allTraegerschaften = traegerschaftService.getAllTraegerschaften();
		Assert.assertEquals(0, allTraegerschaften.size());
	}

}
