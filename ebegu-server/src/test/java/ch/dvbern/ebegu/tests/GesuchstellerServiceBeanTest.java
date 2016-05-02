package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.services.GesuchstellerService;
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
import java.util.Collection;
import java.util.Optional;

/**
 * Tests fuer die Klasse GesuchstellerService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchstellerServiceBeanTest extends AbstractEbeguTest {

	@Inject
	private GesuchstellerService gesuchstellerService;

	@Inject
	private Persistence<Gesuchsteller> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createGesuchsteller() {
		Assert.assertNotNull(gesuchstellerService);
		Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();

		gesuchstellerService.createGesuchsteller(gesuchsteller);
		Collection<Gesuchsteller> allGesuchsteller = gesuchstellerService.getAllGesuchsteller();
		Assert.assertEquals(1, allGesuchsteller.size());
		Gesuchsteller nextGesuchsteller = allGesuchsteller.iterator().next();
		Assert.assertEquals("Tester", nextGesuchsteller.getNachname());
		Assert.assertEquals("tim.tester@example.com", nextGesuchsteller.getMail());
	}

	@Test
	public void updateGesuchstellerTest() {
		Assert.assertNotNull(gesuchstellerService);
		Gesuchsteller insertedGesuchsteller = insertNewEntity();
		Optional<Gesuchsteller> gesuchstellerOptional = gesuchstellerService.findGesuchsteller(insertedGesuchsteller.getId());
		Assert.assertTrue(gesuchstellerOptional.isPresent());
		Gesuchsteller gesuchsteller = gesuchstellerOptional.get();
		Assert.assertEquals("tim.tester@example.com", gesuchsteller.getMail());

		gesuchsteller.setMail("fritz.mueller@example.com");
		Gesuchsteller updatedGesuchsteller = gesuchstellerService.updateGesuchsteller(gesuchsteller);
		Assert.assertEquals("fritz.mueller@example.com", updatedGesuchsteller.getMail());
	}

	@Test
	public void removeGesuchstellerTest() {
		Assert.assertNotNull(gesuchstellerService);
		Gesuchsteller insertedGesuchsteller = insertNewEntity();
		Assert.assertEquals(1, gesuchstellerService.getAllGesuchsteller().size());

		gesuchstellerService.removeGesuchsteller(insertedGesuchsteller);
		Assert.assertEquals(0, gesuchstellerService.getAllGesuchsteller().size());
	}



	// Helper Methods

	private Gesuchsteller insertNewEntity() {
		Gesuchsteller gesuchsteller = TestDataUtil.createDefaultGesuchsteller();
		persistence.persist(gesuchsteller);
		return gesuchsteller;
	}



}
