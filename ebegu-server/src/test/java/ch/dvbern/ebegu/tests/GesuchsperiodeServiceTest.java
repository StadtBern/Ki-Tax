package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.tets.TestDataUtil;
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
 * Arquillian Tests fuer die Klasse GesuchsperiodeService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchsperiodeServiceTest extends AbstractEbeguTest {

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createGesuchsperiodeTest() {
		Assert.assertNotNull(gesuchsperiodeService);
		insertNewEntity();

		Collection<Gesuchsperiode> allGesuchsperioden = gesuchsperiodeService.getAllGesuchsperioden();
		Assert.assertEquals(1, allGesuchsperioden.size());
		Gesuchsperiode nextGesuchsperiode = allGesuchsperioden.iterator().next();
		Assert.assertTrue(nextGesuchsperiode.getActive());
	}

	@Test
	public void updateGesuchsperiodeTest() {
		Assert.assertNotNull(gesuchsperiodeService);
		Gesuchsperiode insertedGesuchsperiode = insertNewEntity();
		Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(insertedGesuchsperiode.getId());
		Assert.assertTrue(gesuchsperiode.get().getActive());

		gesuchsperiode.get().setActive(false);
		Gesuchsperiode updateGesuchsperiode = gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode.get());
		Assert.assertFalse(updateGesuchsperiode.getActive());
		Assert.assertFalse(gesuchsperiodeService.findGesuchsperiode(updateGesuchsperiode.getId()).get().getActive());
	}

	@Test
	public void removeGesuchsperiodeTest() {
		Assert.assertNotNull(gesuchsperiodeService);
		Assert.assertEquals(0, gesuchsperiodeService.getAllGesuchsperioden().size());

		Gesuchsperiode insertedGesuchsperiode = insertNewEntity();
		Assert.assertEquals(1, gesuchsperiodeService.getAllGesuchsperioden().size());

		gesuchsperiodeService.removeGesuchsperiode(insertedGesuchsperiode.getId());
		Assert.assertEquals(0, gesuchsperiodeService.getAllGesuchsperioden().size());
	}


	// HELP METHODS

	private Gesuchsperiode insertNewEntity() {
		Gesuchsperiode gesuchsperiode = TestDataUtil.createDefaultGesuchsperiode();
		gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode);
		return gesuchsperiode;
	}
}
