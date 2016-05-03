package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.services.TraegerschaftService;
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
 * Tests fuer die Klasse TraegerschaftService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class TraegerschaftServiceTest extends AbstractEbeguTest {

	@Inject
	private TraegerschaftService traegerschaftService;

	@Inject
	private Persistence<Traegerschaft> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createTraegerschaft() {
		Assert.assertNotNull(traegerschaftService);
		Traegerschaft traegerschaft = TestDataUtil.createDefaultTraegerschaft();

		traegerschaftService.saveTraegerschaft(traegerschaft);
		Optional<Traegerschaft> traegerschaftOpt = traegerschaftService.findTraegerschaft(traegerschaft.getId());
		Assert.assertTrue(traegerschaftOpt.isPresent());
		Assert.assertEquals("Traegerschaft1", traegerschaftOpt.get().getName());
	}

	@Test
	public void removeTraegerschaft() {
		Assert.assertNotNull(traegerschaftService);
		Traegerschaft traegerschaft = TestDataUtil.createDefaultTraegerschaft();

		traegerschaftService.saveTraegerschaft(traegerschaft);
		Collection<Traegerschaft> allTraegerschaften = traegerschaftService.getAllTraegerschaften();
		Assert.assertEquals(1, allTraegerschaften.size());
		traegerschaftService.removeTraegerschaft(allTraegerschaften.iterator().next().getId());
		allTraegerschaften = traegerschaftService.getAllTraegerschaften();
		Assert.assertEquals(0, allTraegerschaften.size());
	}

}
