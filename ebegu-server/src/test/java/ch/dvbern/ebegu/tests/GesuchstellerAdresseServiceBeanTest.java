package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;
import ch.dvbern.ebegu.services.AdresseService;
import ch.dvbern.ebegu.tets.TestDataUtil;
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
import java.util.Optional;

/**
 * Tests fuer die Klasse AdresseService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchstellerAdresseServiceBeanTest extends AbstractEbeguTest {

	@Inject
	private AdresseService adresseService;

	@Inject
	private Persistence<GesuchstellerAdresse> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}


	@Test
	public void createAdresseTogetherWithGesuchstellerTest() {
		Gesuchsteller gesuchsteller  = TestDataUtil.createDefaultGesuchsteller();
		Gesuchsteller storedGesuchsteller = persistence.persist(gesuchsteller);
		Assert.assertNotNull(storedGesuchsteller.getAdressen());
		Assert.assertTrue(storedGesuchsteller.getAdressen().stream().findAny().isPresent());

	}

	@Test
	public void updateAdresseTest() {
		Assert.assertNotNull(adresseService);
		GesuchstellerAdresse insertedAdresses = insertNewEntity();
		Optional<GesuchstellerAdresse> adresse = adresseService.findAdresse(insertedAdresses.getId());
		Assert.assertEquals("21", adresse.get().getHausnummer());

		adresse.get().setHausnummer("99");
		GesuchstellerAdresse updatedAdr = adresseService.updateAdresse(adresse.get());
		Assert.assertEquals("99", updatedAdr.getHausnummer());
		Assert.assertEquals("99", adresseService.findAdresse(updatedAdr.getId()).get().getHausnummer());
	}

	@Test
	public void removeAdresseTest() {
		Assert.assertNotNull(adresseService);
		GesuchstellerAdresse insertedAdresses = insertNewEntity();
		Assert.assertEquals(1, adresseService.getAllAdressen().size());
		adresseService.removeAdresse(insertedAdresses);
		Assert.assertEquals(0, adresseService.getAllAdressen().size());
	}

	// Help Methods
	private GesuchstellerAdresse insertNewEntity() {
		Gesuchsteller pers = TestDataUtil.createDefaultGesuchsteller();
		Gesuchsteller storedPers =  persistence.persist(pers);
		return storedPers.getAdressen().stream().findAny().orElseThrow(() -> new IllegalStateException("Testdaten nicht korrekt aufgesetzt"));
	}

}
