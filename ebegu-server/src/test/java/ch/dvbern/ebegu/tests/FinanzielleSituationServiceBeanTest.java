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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.FinanzielleSituation;
import ch.dvbern.ebegu.entities.FinanzielleSituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.services.FinanzielleSituationService;
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
 * Tests fuer die Klasse FinanzielleSituationService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class FinanzielleSituationServiceBeanTest extends AbstractEbeguLoginTest {

	@Inject
	private FinanzielleSituationService finanzielleSituationService;

	@Inject
	private Persistence persistence;

	@Test
	public void createFinanzielleSituation() {
		Assert.assertNotNull(finanzielleSituationService);

		FinanzielleSituation finanzielleSituation = TestDataUtil.createDefaultFinanzielleSituation();
		final Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		gesuchsteller = persistence.persist(gesuchsteller);

		FinanzielleSituationContainer container = TestDataUtil.createFinanzielleSituationContainer();
		container.setFinanzielleSituationGS(finanzielleSituation);
		container.setGesuchsteller(gesuchsteller);

		finanzielleSituationService.saveFinanzielleSituation(container, null);
		Collection<FinanzielleSituationContainer> allFinanzielleSituationen = finanzielleSituationService.getAllFinanzielleSituationen();
		Assert.assertEquals(1, allFinanzielleSituationen.size());
		FinanzielleSituationContainer nextFinanzielleSituation = allFinanzielleSituationen.iterator().next();
		Assert.assertEquals(100000L, nextFinanzielleSituation.getFinanzielleSituationGS().getNettolohn().longValue());
	}

	@Test
	public void updateFinanzielleSituationTest() {
		Assert.assertNotNull(finanzielleSituationService);
		FinanzielleSituationContainer insertedFinanzielleSituations = insertNewEntity();
		Optional<FinanzielleSituationContainer> finanzielleSituationOptional = finanzielleSituationService.findFinanzielleSituation(insertedFinanzielleSituations.getId());
		Assert.assertTrue(finanzielleSituationOptional.isPresent());
		FinanzielleSituationContainer finanzielleSituation = finanzielleSituationOptional.get();
		finanzielleSituation.setFinanzielleSituationGS(TestDataUtil.createDefaultFinanzielleSituation());
		FinanzielleSituationContainer updatedCont = finanzielleSituationService.saveFinanzielleSituation(finanzielleSituation, null);
		Assert.assertEquals(100000L, updatedCont.getFinanzielleSituationGS().getNettolohn().longValue());

		updatedCont.getFinanzielleSituationGS().setNettolohn(new BigDecimal(200000));
		FinanzielleSituationContainer contUpdTwice = finanzielleSituationService.saveFinanzielleSituation(updatedCont, null);
		Assert.assertEquals(200000L, contUpdTwice.getFinanzielleSituationGS().getNettolohn().longValue());
	}

	private FinanzielleSituationContainer insertNewEntity() {
		final Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		FinanzielleSituationContainer container = TestDataUtil.createFinanzielleSituationContainer();
		gesuchsteller.setFinanzielleSituationContainer(container);
		gesuchsteller = persistence.persist(gesuchsteller);
		return gesuchsteller.getFinanzielleSituationContainer();
	}
}
