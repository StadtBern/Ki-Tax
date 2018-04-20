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

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.ApplicationProperty;
import ch.dvbern.ebegu.enums.ApplicationPropertyKey;
import ch.dvbern.ebegu.services.ApplicationPropertyService;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Dies ist ein Beispiel einer Arquillian Test Klasse. Es wird vor jedem Test die Datenbank mit dem leeren Dataset
 * initialisiert.
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class ApplicationPropertyServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private ApplicationPropertyService applicationPropertyService;

	@Inject
	private Persistence persistence;

	@Test
	public void saveOrUpdateApplicationPropertyTest() {
		Assert.assertNotNull(applicationPropertyService);
		applicationPropertyService.saveOrUpdateApplicationProperty(ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED, "testValue");
		Assert.assertEquals(1, applicationPropertyService.getAllApplicationProperties().size());
		Assert.assertEquals("testValue", applicationPropertyService.readApplicationProperty(ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED).get().getValue());

	}

	@Test
	public void removeApplicationPropertyTest() {
		insertNewEntity();
		applicationPropertyService.removeApplicationProperty(ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED);
		Assert.assertEquals(0, applicationPropertyService.getAllApplicationProperties().size());

	}

	@Test
	public void updateApplicationPropertyTest() {
		insertNewEntity();
		applicationPropertyService.saveOrUpdateApplicationProperty(ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED, "changed");
		Assert.assertEquals("changed", applicationPropertyService.readApplicationProperty(ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED).get().getValue());

	}

	// Help Methods

	private void insertNewEntity() {
		persistence.persist(new ApplicationProperty(ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED, "testValue"));
		Assert.assertEquals(1, applicationPropertyService.getAllApplicationProperties().size());
		Assert.assertNotNull(applicationPropertyService.readApplicationProperty(ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED));
		Assert.assertEquals("testValue", applicationPropertyService.readApplicationProperty(ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED).get().getValue());
	}

}
