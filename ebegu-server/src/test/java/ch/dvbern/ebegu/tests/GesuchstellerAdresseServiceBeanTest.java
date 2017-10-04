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

import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;
import ch.dvbern.ebegu.entities.GesuchstellerAdresseContainer;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.services.GesuchstellerAdresseService;
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
 * Tests fuer die Klasse AdresseService
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchstellerAdresseServiceBeanTest extends AbstractEbeguLoginTest {

	@Inject
	private GesuchstellerAdresseService adresseService;

	@Inject
	private Persistence persistence;

	@Test
	public void createAdresseTogetherWithGesuchstellerTest() {
		final Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		GesuchstellerContainer gesuchsteller = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		GesuchstellerContainer storedGesuchsteller = persistence.persist(gesuchsteller);
		Assert.assertNotNull(storedGesuchsteller.getAdressen());
		Assert.assertTrue(storedGesuchsteller.getAdressen().stream().findAny().isPresent());

	}

	@Test
	public void updateAdresseTest() {
		Assert.assertNotNull(adresseService);
		GesuchstellerAdresseContainer insertedAdresses = insertNewEntity();
		Optional<GesuchstellerAdresseContainer> adresse = adresseService.findAdresse(insertedAdresses.getId());
		Assert.assertEquals("21", adresse.get().extractHausnummer());

		adresse.get().getGesuchstellerAdresseJA().setHausnummer("99");
		GesuchstellerAdresseContainer updatedAdr = adresseService.updateAdresse(adresse.get());
		Assert.assertEquals("99", updatedAdr.extractHausnummer());
		Assert.assertEquals("99", adresseService.findAdresse(updatedAdr.getId()).get().extractHausnummer());
	}

	@Test
	public void findKorrAddresse() {
		Assert.assertNotNull(adresseService);
		final GesuchstellerContainer gesuchstellerContainer = insertNewEntityWithKorrespondenzadresse();
		Optional<GesuchstellerAdresseContainer> adresse = adresseService.getKorrespondenzAdr(gesuchstellerContainer.getId());
		Assert.assertTrue(adresse.isPresent());

	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@Test
	public void removeAdresseTest() {
		Assert.assertNotNull(adresseService);
		GesuchstellerAdresseContainer insertedAdresses = insertNewEntity();
		Assert.assertEquals(1, adresseService.getAllAdressen().size());
		adresseService.removeAdresse(insertedAdresses);
		Assert.assertEquals(0, adresseService.getAllAdressen().size());
	}

	// Help Methods
	private GesuchstellerAdresseContainer insertNewEntity() {
		final Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		GesuchstellerContainer pers = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		GesuchstellerContainer storedPers = persistence.persist(pers);
		return storedPers.getAdressen().stream().findAny().orElseThrow(() -> new IllegalStateException("Testdaten nicht korrekt aufgesetzt"));
	}

	private GesuchstellerContainer insertNewEntityWithKorrespondenzadresse() {
		final Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		GesuchstellerContainer pers = TestDataUtil.createDefaultGesuchstellerContainer(gesuch);
		GesuchstellerContainer storedPers = persistence.persist(pers);

		GesuchstellerAdresseContainer korrAddr = TestDataUtil.createDefaultGesuchstellerAdresseContainer(storedPers);
		korrAddr.getGesuchstellerAdresseJA().setAdresseTyp(AdresseTyp.KORRESPONDENZADRESSE);
		storedPers.addAdresse(korrAddr);
		GesuchstellerAdresse gsAddresse = korrAddr.getGesuchstellerAdresseJA().copyForMutation(new GesuchstellerAdresse());
		korrAddr.setGesuchstellerAdresseGS(gsAddresse);
		korrAddr.setGesuchstellerAdresseJA(null);

		return persistence.merge(storedPers);
	}

}
