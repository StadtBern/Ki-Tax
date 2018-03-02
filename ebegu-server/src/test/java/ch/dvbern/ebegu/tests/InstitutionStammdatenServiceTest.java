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

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import ch.dvbern.oss.lib.beanvalidation.embeddables.IBAN;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests fuer die Klasse PersonService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class InstitutionStammdatenServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private InstitutionStammdatenService institutionStammdatenService;

	@Inject
	private Persistence persistence;

	@Test
	public void createPersonInstitutionStammdatenTest() {
		Assert.assertNotNull(institutionStammdatenService);
		InstitutionStammdaten insertedInstitutionStammdaten = insertInstitutionStammdaten();

		Collection<InstitutionStammdaten> allInstitutionStammdaten = institutionStammdatenService.getAllInstitutionStammdaten();
		Assert.assertEquals(1, allInstitutionStammdaten.size());
		InstitutionStammdaten nextInstitutionStammdaten = allInstitutionStammdaten.iterator().next();
		Assert.assertEquals(insertedInstitutionStammdaten.getIban(), nextInstitutionStammdaten.getIban());
		Assert.assertEquals(insertedInstitutionStammdaten.getBetreuungsangebotTyp(), nextInstitutionStammdaten.getBetreuungsangebotTyp());
	}

	@Test
	public void updateInstitutionStammdatenTest() {
		Assert.assertNotNull(institutionStammdatenService);
		InstitutionStammdaten insertedInstitutionStammdaten = insertInstitutionStammdaten();

		Optional<InstitutionStammdaten> institutionStammdatenOptional = institutionStammdatenService.findInstitutionStammdaten(insertedInstitutionStammdaten.getId());
		Assert.assertTrue(institutionStammdatenOptional.isPresent());
		InstitutionStammdaten persistedInstStammdaten = institutionStammdatenOptional.get();
		Assert.assertEquals(insertedInstitutionStammdaten.getIban(), persistedInstStammdaten.getIban());

		persistedInstStammdaten.setIban(new IBAN("CH39 0900 0000 3066 3817 2"));
		InstitutionStammdaten updatedInstitutionStammdaten = institutionStammdatenService.saveInstitutionStammdaten(persistedInstStammdaten);
		Assert.assertEquals(persistedInstStammdaten.getIban(), updatedInstitutionStammdaten.getIban());
	}

	@Test
	public void getAllInstitutionStammdatenByDateTest() {
		Assert.assertNotNull(institutionStammdatenService);
		InstitutionStammdaten insertedInstitutionStammdaten = insertInstitutionStammdaten();

		Collection<InstitutionStammdaten> allInstitutionStammdatenByDate = institutionStammdatenService.getAllInstitutionStammdatenByDate(LocalDate.now());
		Assert.assertEquals(0, allInstitutionStammdatenByDate.size());

		insertedInstitutionStammdaten.setGueltigkeit(new DateRange(LocalDate.of(2010, 1, 1), Constants.END_OF_TIME));
		institutionStammdatenService.saveInstitutionStammdaten(insertedInstitutionStammdaten);
		Collection<InstitutionStammdaten> allInstitutionStammdatenByDate2 = institutionStammdatenService.getAllInstitutionStammdatenByDate(LocalDate.now());
		Assert.assertEquals(1, allInstitutionStammdatenByDate2.size());
	}

	@Test
	public void getAllInstitutionStammdatenByInstitution() {
		Assert.assertNotNull(institutionStammdatenService);
		InstitutionStammdaten insertedInstitutionStammdaten = insertInstitutionStammdaten();
		String id = insertedInstitutionStammdaten.getInstitution().getId();
		Collection<InstitutionStammdaten> allInstitutionStammdatenByInstitution = institutionStammdatenService.getAllInstitutionStammdatenByInstitution(id);
		Assert.assertEquals(1, allInstitutionStammdatenByInstitution.size());

	}

	@Test
	public void getAllActiveInstitutionStammdatenByGesuchsperiode() {
		Mandant mandant = TestDataUtil.createDefaultMandant();
		mandant = persistence.persist(mandant);
		Gesuchsperiode gesuchsperiode1718 = TestDataUtil.createGesuchsperiode1718();
		gesuchsperiode1718 = persistence.persist(gesuchsperiode1718);
		Institution institution = TestDataUtil.createDefaultInstitution();
		institution.setTraegerschaft(null);
		institution.setMandant(mandant);
		institution = persistence.persist(institution);
		LocalDate gpStart = gesuchsperiode1718.getGueltigkeit().getGueltigAb();
		LocalDate gpEnde = gesuchsperiode1718.getGueltigkeit().getGueltigBis();

		InstitutionStammdaten isIdentisch = addInstitutionsstammdaten(institution, gpStart, gpEnde);
		InstitutionStammdaten schnittAnfang = addInstitutionsstammdaten(institution, gpStart.minusWeeks(1), gpStart.plusWeeks(1));
		InstitutionStammdaten schnittEnde = addInstitutionsstammdaten(institution, gpEnde.minusWeeks(1), gpEnde.plusWeeks(1));
		InstitutionStammdaten schnittMitte = addInstitutionsstammdaten(institution, gpStart.plusWeeks(1), gpEnde.minusWeeks(1));
		InstitutionStammdaten ueberlappendTotal = addInstitutionsstammdaten(institution, gpStart.minusWeeks(1), gpEnde.plusWeeks(1));
		InstitutionStammdaten completelyBefore = addInstitutionsstammdaten(institution, gpStart.minusWeeks(2), gpStart.minusWeeks(1));
		InstitutionStammdaten completelyAfter = addInstitutionsstammdaten(institution, gpEnde.plusWeeks(1), gpEnde.plusWeeks(2));

		Collection<InstitutionStammdaten> all = institutionStammdatenService.getAllActiveInstitutionStammdatenByGesuchsperiode(gesuchsperiode1718.getId());
		Assert.assertNotNull(all);
		Assert.assertEquals(5, all.size());

		Assert.assertTrue(all.contains(isIdentisch));
		Assert.assertTrue(all.contains(schnittAnfang));
		Assert.assertTrue(all.contains(schnittEnde));
		Assert.assertTrue(all.contains(schnittMitte));
		Assert.assertTrue(all.contains(ueberlappendTotal));

		Assert.assertFalse(all.contains(completelyBefore));
		Assert.assertFalse(all.contains(completelyAfter));
	}

	// HELP METHODS

	private InstitutionStammdaten insertInstitutionStammdaten() {
		InstitutionStammdaten institutionStammdaten = TestDataUtil.createDefaultInstitutionStammdaten();
		persistence.persist(institutionStammdaten.getInstitution().getMandant());
		persistence.persist(institutionStammdaten.getInstitution().getTraegerschaft());
		persistence.persist(institutionStammdaten.getInstitution());
		persistence.persist(institutionStammdaten.getAdresse());
		return institutionStammdatenService.saveInstitutionStammdaten(institutionStammdaten);
	}

	private InstitutionStammdaten addInstitutionsstammdaten(Institution institution, LocalDate start, LocalDate end) {
		InstitutionStammdaten institutionStammdaten = TestDataUtil.createDefaultInstitutionStammdaten();
		institutionStammdaten.setInstitution(institution);
		institutionStammdaten.getGueltigkeit().setGueltigAb(start);
		institutionStammdaten.getGueltigkeit().setGueltigBis(end);
		return institutionStammdatenService.saveInstitutionStammdaten(institutionStammdaten);
	}

}
