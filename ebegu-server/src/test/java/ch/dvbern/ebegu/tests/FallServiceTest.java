package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.services.FallService;
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

/**
 * Arquillian Tests fuer die Klasse FallService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class FallServiceTest extends AbstractEbeguTest {

	@Inject
	private FallService fallService;

	@Inject
	private Persistence<Fall> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createFall() {
		Assert.assertNotNull(fallService);
		Fall fall = TestDataUtil.createDefaultFall();
		fallService.saveFall(fall);

		Collection<Fall> allFalle = fallService.getAllFalle();
		Assert.assertEquals(1, allFalle.size());
	}

	@Test
	public void removeFallTest() {
		Assert.assertNotNull(fallService);
		Fall fall = TestDataUtil.createDefaultFall();
		fallService.saveFall(fall);
		Assert.assertEquals(1, fallService.getAllFalle().size());

		fallService.removeFall(fall);
		Assert.assertEquals(0, fallService.getAllFalle().size());
	}

}
