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

import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.services.InstitutionService;
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
 * Tests fuer die Klasse InstitutionService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class InstitutionServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private InstitutionService institutionService;
	@Inject
	private Persistence persistence;

	@Test
	public void createInstitution() {
		Assert.assertNotNull(institutionService);
		Institution institution = insertInstitution();

		Optional<Institution> institutionOpt = institutionService.findInstitution(institution.getId());
		Assert.assertTrue(institutionOpt.isPresent());
		Assert.assertEquals("Institution1", institutionOpt.get().getName());
		Assert.assertEquals(institutionOpt.get().getMandant().getId(), institution.getMandant().getId());
		Assert.assertEquals(institutionOpt.get().getTraegerschaft().getId(), institution.getTraegerschaft().getId());
	}

	@Test
	public void deleteInstitution() {
		Assert.assertNotNull(institutionService);
		Institution institution = insertInstitution();

		Optional<Institution> institutionOpt = institutionService.findInstitution(institution.getId());
		Assert.assertTrue(institutionOpt.isPresent());
		institutionService.deleteInstitution(institutionOpt.get().getId());
		Optional<Institution> institutionOpt2 = institutionService.findInstitution(institution.getId());
		Assert.assertFalse(institutionOpt2.isPresent());
	}

	// This test gives a really strange Error java.lang.NoSuchMethodError: ch.dvbern.ebegu.entities.Institution.setActive(Ljava/lang/Boolean;)V
	// but the method in the entity is definitely there!
	@Test
	public void inactiveInstitution() {
		Assert.assertNotNull(institutionService);
		Institution institution = insertInstitution();

		Optional<Institution> institutionOpt = institutionService.findInstitution(institution.getId());
		Assert.assertTrue(institutionOpt.isPresent());
		Institution inavtiveInst = institutionService.setInstitutionInactive(institutionOpt.get().getId());
		Optional<Institution> institutionOpt2 = institutionService.findInstitution(institution.getId());
		Assert.assertFalse(inavtiveInst.getActive());
	}

	@Test
	public void getAllInstitutionenTest() {
		Assert.assertNotNull(institutionService);
		Institution institution = insertInstitution();

		Collection<Institution> allInstitutionen = institutionService.getAllInstitutionen();
		Assert.assertFalse(allInstitutionen.isEmpty());

	}

	// HELP METHODS

	private Institution insertInstitution() {
		Institution institution = TestDataUtil.createDefaultInstitution();

		Traegerschaft traegerschaft = TestDataUtil.createDefaultTraegerschaft();
		persistence.persist(traegerschaft);
		institution.setTraegerschaft(traegerschaft);

		Mandant mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		institution.setMandant(mandant);

		institutionService.createInstitution(institution);
		return institution;
	}

}
