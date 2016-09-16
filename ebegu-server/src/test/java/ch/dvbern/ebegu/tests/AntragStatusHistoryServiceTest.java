package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.services.AntragStatusHistoryService;
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
import java.time.LocalDateTime;

/**
 * Arquillian Tests fuer die Klasse AntragStatusHistoryService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class AntragStatusHistoryServiceTest extends AbstractEbeguTest {

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Inject
	private AntragStatusHistoryService statusHistoryService;
	@Inject
	private Persistence<Gesuch> persistence;


	@Test
	public void saveChanges() {
		final Benutzer benutzer = TestDataUtil.createAndPersistBenutzer(persistence);
		final Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);

		LocalDateTime time = LocalDateTime.now();
		final AntragStatusHistory createdStatusHistory = statusHistoryService.saveStatusChange(gesuch);

		Assert.assertNotNull(createdStatusHistory);
		Assert.assertEquals(gesuch.getStatus(), createdStatusHistory.getStatus());
		Assert.assertEquals(gesuch, createdStatusHistory.getGesuch());
		Assert.assertEquals(benutzer, createdStatusHistory.getBenutzer());
		//just check that the generated date is after (or equals) the temporal one we created before
		Assert.assertTrue(time.isBefore(createdStatusHistory.getDatum()) || time.isEqual(createdStatusHistory.getDatum()));
	}

}
