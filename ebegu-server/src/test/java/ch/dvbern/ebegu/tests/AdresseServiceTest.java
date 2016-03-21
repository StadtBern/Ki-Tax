package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.enums.Land;
import ch.dvbern.ebegu.services.AdresseService;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;

/**
 * Created by imanol on 18.03.16.
 * Tests fuer die Klasse AdresseService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class AdresseServiceTest extends AbstractEbeguTest {

	@Inject
	private AdresseService adresseService;

	@Inject
	private Persistence<Adresse> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createAdressTest() {
		Assert.assertNotNull(adresseService);
		Adresse adresse = TestDataUtil.createDefaultAdresse();

		adresseService.createAdresse(adresse);
		Collection<Adresse> allAdressen = adresseService.getAllAdressen();
		Assert.assertEquals(1, allAdressen.size());
		Adresse nextAdresse = allAdressen.iterator().next();
		Assert.assertEquals("Nussbaumstrasse", nextAdresse.getStrasse());
		Assert.assertEquals("21", nextAdresse.getHausnummer());
		Assert.assertEquals(Land.CH, nextAdresse.getLand());
	}

	@Test
	public void updateAdressTest() {
		Assert.assertNotNull(adresseService);
		Adresse insertedAdresses = insertNewEntity();
		Optional<Adresse> adresse = adresseService.findAdresse(insertedAdresses.getId());
		Assert.assertEquals("21", adresse.get().getHausnummer());

		adresse.get().setHausnummer("99");
		adresseService.updateAdresse(adresse.get());
		Assert.assertEquals("99", adresse.get().getHausnummer());
	}

	@Test
	public void removeAdressTest() {
		Assert.assertNotNull(adresseService);
		Adresse insertedAdresses = insertNewEntity();
		Assert.assertEquals(1, adresseService.getAllAdressen().size());

		adresseService.removeAdresse(insertedAdresses);
		Assert.assertEquals(0, adresseService.getAllAdressen().size());
	}

	// Help Methods

	private Adresse insertNewEntity() {
		Adresse adresse = TestDataUtil.createDefaultAdresse();
		persistence.persist(adresse);
		return adresse;
	}



}