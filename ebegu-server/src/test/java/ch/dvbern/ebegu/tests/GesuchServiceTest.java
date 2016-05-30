package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.services.GesuchService;
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
 * Arquillian Tests fuer die Klasse GesuchService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchServiceTest extends AbstractEbeguTest {

	@Inject
	private GesuchService gesuchService;

	@Inject
	private Persistence<Gesuch> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createGesuch() {
		Assert.assertNotNull(gesuchService);
		persistNewEntity();

		Collection<Gesuch> allGesuche = gesuchService.getAllGesuche();
		Assert.assertEquals(1, allGesuche.size());
	}

	@Test
	public void updateGesuch() {
		Assert.assertNotNull(gesuchService);
		Gesuch insertedGesuch = persistNewEntity();

		Optional<Gesuch> gesuch = gesuchService.findGesuch(insertedGesuch.getId());
		Assert.assertEquals(insertedGesuch.getFall().getId(), gesuch.get().getFall().getId());

		gesuch.get().setFall(persistence.persist(TestDataUtil.createDefaultFall()));
		Gesuch updated = gesuchService.updateGesuch(gesuch.get());
		Assert.assertEquals(updated.getFall().getId(), gesuch.get().getFall().getId());

	}

	@Test
	public void removeGesuchTest() {
		Assert.assertNotNull(gesuchService);
		Gesuch gesuch = persistNewEntity();
		Assert.assertEquals(1, gesuchService.getAllGesuche().size());

		gesuchService.removeGesuch(gesuch);
		Assert.assertEquals(0, gesuchService.getAllGesuche().size());
	}


	// HELP METHOD

	private Gesuch persistNewEntity() {
		Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		gesuch.setGesuchsperiode(persistence.persist(gesuch.getGesuchsperiode()));
		gesuch.setFall(persistence.persist(gesuch.getFall()));
		gesuchService.createGesuch(gesuch);
		return gesuch;
	}

}
