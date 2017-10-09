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

import java.util.List;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.ApplicationProperty;
import ch.dvbern.ebegu.enums.ApplicationPropertyKey;
import ch.dvbern.ebegu.services.HistorizationService;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests fuer Services von Historization. Es wird vor jedem Test die Datenbank mit dem leeren Dataset
 * initialisiert.
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/applicationPropertyAudited.xml")
@Transactional(TransactionMode.DISABLED)
public class HistorizationServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private HistorizationService historizationService;

	@Inject
	private Persistence persistence;

	@Test
	public void getAllEntitiesByRevisionTest() {
		Assert.assertNotNull(historizationService);
		assertValuesAtRevision(1, ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED, "entry1");
		assertValuesAtRevision(2, ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED, "entry2");
	}

	@Test
	public void getAllRevisionsByIdTest() {
		List<Object[]> revisions = historizationService
			.getAllRevisionsById(ApplicationProperty.class.getSimpleName(), "67219837-0e12-4d97-b245-649b90d9d2a5");

		Assert.assertNotNull(revisions);
		Assert.assertEquals(2, revisions.size());
		checkRevisionsValues(revisions.get(0), ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED, "entry1", 1, RevisionType.ADD);
		checkRevisionsValues(revisions.get(1), ApplicationPropertyKey.EVALUATOR_DEBUG_ENABLED, "entry2", 2, RevisionType.MOD);

	}

	// Help Methods

	private void checkRevisionsValues(Object[] revisionObject, ApplicationPropertyKey name, String value, int rev, RevisionType revisionType) {
		Assert.assertTrue(revisionObject[0] instanceof AbstractEntity);
		Assert.assertTrue(revisionObject[1] instanceof DefaultRevisionEntity);
		Assert.assertTrue(revisionObject[2] instanceof RevisionType);

		Assert.assertEquals(name, ((ApplicationProperty) revisionObject[0]).getName());
		Assert.assertEquals(value, ((ApplicationProperty) revisionObject[0]).getValue());
		Assert.assertEquals(rev, ((DefaultRevisionEntity) revisionObject[1]).getId());
		Assert.assertEquals(revisionType, revisionObject[2]);
	}

	private void assertValuesAtRevision(Integer revision, ApplicationPropertyKey name, String value) {
		List<AbstractEntity> entities = historizationService
			.getAllEntitiesByRevision(ApplicationProperty.class.getSimpleName(), revision);

		Assert.assertNotNull(entities);
		Assert.assertEquals(1, entities.size());
		Assert.assertTrue(entities.get(0) instanceof ApplicationProperty);
		Assert.assertEquals(name, ((ApplicationProperty) entities.get(0)).getName());
		Assert.assertEquals(value, ((ApplicationProperty) entities.get(0)).getValue());
	}

}
