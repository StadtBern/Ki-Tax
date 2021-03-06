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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.services.FallService;
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
 * Arquillian Tests fuer die Klasse FallService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)  //disabeln sonst existiert in changeVerantwortlicherOfFallTest der Benutzer noch gar nicht
public class FallServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private FallService fallService;

	@Inject
	private Persistence persistence;

	@Inject
	private InstitutionService institutionService;

	@Test
	public void createFallTest() {
		Assert.assertNotNull(fallService);
		Fall fall = TestDataUtil.createDefaultFall();
		fallService.saveFall(fall);

		Collection<Fall> allFalle = fallService.getAllFalle(false);
		Assert.assertEquals(1, allFalle.size());
		Assert.assertEquals(1, allFalle.iterator().next().getFallNummer());

		Assert.assertNotNull(fallService);
		Fall secondFall = TestDataUtil.createDefaultFall();
		fallService.saveFall(secondFall);

		//Wir erwarten das die Fallnummern 1 und 2 (bzw in PSQL 0 und 1 ) vergeben wurden
		List<Fall> moreFaelle = new ArrayList<>(fallService.getAllFalle(false).stream()
			.sorted((o1, o2) -> Long.valueOf(o1.getFallNummer()).compareTo(Long.valueOf(o2.getFallNummer())))
			.collect(Collectors.toList()));
		Assert.assertEquals(2, moreFaelle.size());
		for (int i = 0; i < moreFaelle.size(); i++) {
			int expectedFallNr = (i + 1); //H2 DB faengt anscheinend im Gegensatz zu PSQL bei 1 an wenn auto increment
			Assert.assertEquals(expectedFallNr, moreFaelle.get(i).getFallNummer());
		}
	}

	@Test
	public void changeVerantwortlicherOfFallTest() {
		Fall fall = TestDataUtil.createDefaultFall();
		Fall savedFall = fallService.saveFall(fall);

		Optional<Fall> loadedFallOpt = fallService.findFall(savedFall.getId());
		Assert.assertTrue(loadedFallOpt.isPresent());
		Fall loadedFall = loadedFallOpt.get();
		Assert.assertNull(loadedFall.getVerantwortlicher());
		Benutzer benutzerToSet = getDummySuperadmin();
		Benutzer storedBenutzer = persistence.find(Benutzer.class, benutzerToSet.getId());
		loadedFall.setVerantwortlicher(storedBenutzer);

		Fall updatedFall = fallService.saveFall(loadedFall);
		Assert.assertNotNull(loadedFall.getVerantwortlicher());
		Assert.assertEquals(benutzerToSet.getId(), updatedFall.getVerantwortlicher().getId());

	}

	@Test
	public void removeFallTest() {
		Assert.assertNotNull(fallService);
		Fall fall = TestDataUtil.createDefaultFall();
		fallService.saveFall(fall);
		Assert.assertEquals(1, fallService.getAllFalle(false).size());

		fallService.removeFall(fall);
		Assert.assertEquals(0, fallService.getAllFalle(false).size());
	}

	@Test
	public void testCreateFallForGSNoGS() {
		loginAsSachbearbeiterJA();

		final Optional<Fall> fall = fallService.createFallForCurrentGesuchstellerAsBesitzer();
		Assert.assertFalse(fall.isPresent());
	}

	@Test
	public void testCreateFallForGSTwoTimes() {
		loginAsGesuchsteller("gesuchst");

		final Optional<Fall> fall = fallService.createFallForCurrentGesuchstellerAsBesitzer();
		Assert.assertTrue(fall.isPresent());
		Assert.assertEquals("gesuchst", fall.get().getBesitzer().getUsername());

		final Optional<Fall> fall2 = fallService.createFallForCurrentGesuchstellerAsBesitzer();
		Assert.assertFalse(fall2.isPresent()); // if a fall already exists for this GS it is not created again
	}

	@Test
	public void testCreateFallForTwoDifferentGS() {
		loginAsGesuchsteller("gesuchst");
		final Optional<Fall> fall = fallService.createFallForCurrentGesuchstellerAsBesitzer();
		Assert.assertTrue(fall.isPresent());
		Assert.assertEquals("gesuchst", fall.get().getBesitzer().getUsername());

		loginAsGesuchsteller("gesuchst2");
		final Optional<Fall> fall2 = fallService.createFallForCurrentGesuchstellerAsBesitzer();
		Assert.assertTrue(fall2.isPresent()); // if a fall already exists for this GS it is not created again
		Assert.assertEquals("gesuchst2", fall2.get().getBesitzer().getUsername());
	}

	@Test
	public void testGetEmailAddressForFallFromFall() {
		loginAsGesuchsteller("gesuchst");
		final Optional<Fall> fallOpt = fallService.createFallForCurrentGesuchstellerAsBesitzer();
		Assert.assertTrue(fallOpt.isPresent());
		Fall fall = fallOpt.get();
		Assert.assertEquals("e@e", fall.getBesitzer().getEmail());
		Assert.assertEquals("gesuchst", fall.getBesitzer().getUsername());
		Optional<String> emailAddressForFall = fallService.getCurrentEmailAddress(fall.getId());
		Assert.assertTrue(emailAddressForFall.isPresent());
		String email = emailAddressForFall.get();
		Assert.assertEquals("e@e", email);
	}

	@Test
	public void testGetEmailAddressForFallFromGS() {
		loginAsGesuchsteller("gesuchst");
		Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, null);

		Assert.assertNotNull(gesuch.getGesuchsteller1().getGesuchstellerJA().getMail());
		Assert.assertNotNull(gesuch.getFall().getBesitzer());
		Assert.assertFalse(gesuch.getFall().getBesitzer().getEmail().equals(gesuch.getGesuchsteller1().getGesuchstellerJA().getMail()));

		Optional<String> emailAddressForFall = fallService.getCurrentEmailAddress(gesuch.getFall().getId());
		Assert.assertTrue(emailAddressForFall.isPresent());
		String email = emailAddressForFall.get();
		Assert.assertEquals("test@example.com", email);

	}

}
